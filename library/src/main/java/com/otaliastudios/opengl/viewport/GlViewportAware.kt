package com.otaliastudios.opengl.viewport

import android.opengl.GLES20

abstract class GlViewportAware {

    private val viewportArray = IntArray(4)

    var viewportWidth: Int = -1
        protected set

    var viewportHeight: Int = -1
        protected set

    fun setViewportSize(width: Int, height: Int) {
        if (width != viewportWidth || height != viewportHeight) {
            viewportWidth = width
            viewportHeight = height
            onViewportSizeChanged()
        }
    }

    protected open fun onViewportSizeChanged() {
        // Do nothing.
    }

    protected fun ensureViewportSize() {
        if (viewportHeight == -1 || viewportWidth == -1) {
            GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewportArray, 0)
            setViewportSize(viewportArray[2], viewportArray[3])
        }
    }
}