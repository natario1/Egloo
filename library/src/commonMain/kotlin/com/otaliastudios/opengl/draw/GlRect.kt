package com.otaliastudios.opengl.draw

import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.geometry.RectF
import com.otaliastudios.opengl.types.floatBuffer
import com.otaliastudios.opengl.internal.GL_TRIANGLE_STRIP
import com.otaliastudios.opengl.internal.glDrawArrays
import com.otaliastudios.opengl.types.FloatBuffer

@Suppress("unused")
public open class GlRect: Gl2dDrawable() {

    private companion object {
        // A full square, extending from -1 to +1 in both dimensions.
        // When the MVP matrix is identity, this will exactly cover the viewport.
        private val FULL_RECTANGLE_COORDS = floatArrayOf(
                -1.0f, -1.0f, // bottom left
                1.0f, -1.0f,  // bottom right
                -1.0f, 1.0f,  // top left
                1.0f, 1.0f)   // top right
    }

    override var vertexArray: FloatBuffer = floatBuffer(FULL_RECTANGLE_COORDS.size).also {
        it.put(FULL_RECTANGLE_COORDS)
        it.clear()
    }

    @Suppress("unused")
    @Deprecated("Use setRect", ReplaceWith("setRect(rect)"))
    public open fun setVertexArray(array: FloatArray) {
        if (array.size != 4 * coordsPerVertex) {
            throw IllegalArgumentException("Vertex array should have 8 values.")
        }
        vertexArray.clear()
        vertexArray.put(array)
        vertexArray.flip()
        notifyVertexArrayChange()
    }

    @Deprecated("Use setRect", ReplaceWith("setRect(rect)"))
    public open fun setVertexArray(rect: RectF) {
        setRect(rect)
    }

    @Suppress("unused")
    public fun setRect(rect: RectF) {
        setRect(rect.left, rect.top, rect.right, rect.bottom)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    public fun setRect(left: Float, top: Float, right: Float, bottom: Float) {
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
        notifyVertexArrayChange()
    }

    override fun draw() {
        Egloo.checkGlError("glDrawArrays start")
        glDrawArrays(GL_TRIANGLE_STRIP, 0, vertexCount)
        Egloo.checkGlError("glDrawArrays end")
    }
}