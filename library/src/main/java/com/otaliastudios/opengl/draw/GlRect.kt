package com.otaliastudios.opengl.draw

import android.graphics.RectF
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.extensions.floatBufferOf
import java.lang.IllegalArgumentException
import java.nio.FloatBuffer

@Suppress("unused")
open class GlRect: GlDrawable() {

    companion object {
        // A full square, extending from -1 to +1 in both dimensions.
        // When the MVP matrix is identity, this will exactly cover the viewport.
        private val FULL_RECTANGLE_COORDS = floatBufferOf(
                -1.0f, -1.0f, // bottom left
                1.0f, -1.0f,  // bottom right
                -1.0f, 1.0f,  // top left
                1.0f, 1.0f)   // top right
    }

    override val coordsPerVertex = 2

    override var vertexArray: FloatBuffer = FULL_RECTANGLE_COORDS

    @Suppress("unused")
    open fun setVertexArray(array: FloatArray) {
        if (array.size != 4 * coordsPerVertex) {
            throw IllegalArgumentException("Vertex array should have 8 values.")
        }
        vertexArray.clear()
        vertexArray.put(array)
        vertexArray.rewind()
    }

    open fun setVertexArray(rect: RectF) {
        vertexArray.clear()
        // 1
        vertexArray.put(rect.left)
        vertexArray.put(rect.bottom)
        // 2
        vertexArray.put(rect.right)
        vertexArray.put(rect.bottom)
        // 3
        vertexArray.put(rect.left)
        vertexArray.put(rect.top)
        // 4
        vertexArray.put(rect.right)
        vertexArray.put(rect.top)
        vertexArray.rewind()
    }

    override fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount)
        Egloo.checkGlError("glDrawArrays")
    }
}