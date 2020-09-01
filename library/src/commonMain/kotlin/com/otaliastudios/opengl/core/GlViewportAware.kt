package com.otaliastudios.opengl.core

import com.otaliastudios.opengl.internal.GL_VIEWPORT
import com.otaliastudios.opengl.internal.glGetIntegerv

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
            glGetIntegerv(GL_VIEWPORT.toUInt(), viewportArray)
            setViewportSize(viewportArray[2], viewportArray[3])
        }
    }
}