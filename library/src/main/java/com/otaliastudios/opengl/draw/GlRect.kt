package com.otaliastudios.opengl.draw

import android.graphics.RectF
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.extensions.floatBufferOf
import java.lang.IllegalArgumentException
import java.nio.FloatBuffer

@Suppress("unused")
open class GlRect: Gl2dDrawable() {

    companion object {
        // A full square, extending from -1 to +1 in both dimensions.
        // When the MVP matrix is identity, this will exactly cover the viewport.
        private val FULL_RECTANGLE_COORDS = floatArrayOf(
                -1.0f, -1.0f, // bottom left
                1.0f, -1.0f,  // bottom right
                -1.0f, 1.0f,  // top left
                1.0f, 1.0f)   // top right
    }

    override var vertexArray: FloatBuffer = floatBufferOf(*FULL_RECTANGLE_COORDS.clone())

    @Suppress("unused")
    @Deprecated("Use setRect", ReplaceWith("setRect(rect)"))
    open fun setVertexArray(array: FloatArray) {
        if (array.size != 4 * coordsPerVertex) {
            throw IllegalArgumentException("Vertex array should have 8 values.")
        }
        vertexArray.clear()
        vertexArray.put(array)
        vertexArray.flip()
    }

    @Deprecated("Use setRect", ReplaceWith("setRect(rect)"))
    open fun setVertexArray(rect: RectF) {
        setRect(rect)
    }

    @Suppress("unused")
    fun setRect(rect: RectF) {
        setRect(rect.left, rect.top, rect.right, rect.bottom)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setRect(left: Float, top: Float, right: Float, bottom: Float) {
        vertexArray.clear()
        // 1
        vertexArray.put(left)
        vertexArray.put(bottom)
        // 2
        vertexArray.put(right)
        vertexArray.put(bottom)
        // 3
        vertexArray.put(left)
        vertexArray.put(top)
        // 4
        vertexArray.put(right)
        vertexArray.put(top)
        vertexArray.flip()
    }

    override fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount)
        Egloo.checkGlError("glDrawArrays")
    }
}