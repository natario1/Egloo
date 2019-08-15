package com.otaliastudios.opengl.draw

import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egl
import com.otaliastudios.opengl.extensions.floatBufferOf
import com.otaliastudios.opengl.extensions.toBuffer
import java.nio.FloatBuffer

@Suppress("unused")
open class EglRect: EglDrawable() {

    companion object {
        // A full square, extending from -1 to +1 in both dimensions.
        // When the model/view/projection matrix is identity, this will exactly cover the viewport.
        private val FULL_RECTANGLE_COORDS = floatBufferOf(
                -1.0f, -1.0f, // 0 bottom left
                1.0f, -1.0f, // 1 bottom right
                -1.0f, 1.0f, // 2 top left
                1.0f, 1.0f) // 3 top right
    }

    override val coordsPerVertex = 2

    override var vertexArray: FloatBuffer = FULL_RECTANGLE_COORDS

    @Suppress("unused")
    fun setVertexArray(array: FloatArray) {
        vertexArray = array.toBuffer()
    }

    override val vertexCount: Int
        get() = vertexArray.capacity() / coordsPerVertex

    override fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount)
        Egl.checkGlError("glDrawArrays")
    }
}