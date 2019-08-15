package com.otaliastudios.opengl.program


import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egl
import com.otaliastudios.opengl.draw.EglDrawable

/**
 * An [EglProgram] that uses basic flat-shading rendering,
 * based on FlatShadedProgram from grafika.
 */
@Suppress("unused")
open class EglFlatProgram : EglProgram(VERTEX_SHADER, FRAGMENT_SHADER) {

    private val vertexPositionHandle = getAttribHandle("aPosition")
    private val vertexMvpMatrixHandle = getUniformHandle("uMVPMatrix")
    private val fragmentColorHandle = getUniformHandle("uColor")

    @Suppress("MemberVisibilityCanBePrivate")
    var color: FloatArray = floatArrayOf(1F, 1F, 1F, 1F)

    override fun onPreDraw(drawable: EglDrawable, modelViewProjectionMatrix: FloatArray) {
        super.onPreDraw(drawable, modelViewProjectionMatrix)

        // Copy the modelViewProjectionMatrix over.
        GLES20.glUniformMatrix4fv(vertexMvpMatrixHandle.value, 1, false,
                modelViewProjectionMatrix, 0)
        Egl.checkGlError("glUniformMatrix4fv")

        // Copy the color vector in.
        GLES20.glUniform4fv(fragmentColorHandle.value, 1, color, 0)
        Egl.checkGlError("glUniform4fv")

        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(vertexPositionHandle.value)
        Egl.checkGlError("glEnableVertexAttribArray")

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(vertexPositionHandle.value, drawable.coordsPerVertex,
                GLES20.GL_FLOAT, false, drawable.vertexStride, drawable.vertexArray)
        Egl.checkGlError("glVertexAttribPointer")
    }

    override fun onPostDraw(drawable: EglDrawable) {
        super.onPostDraw(drawable)
        GLES20.glDisableVertexAttribArray(vertexPositionHandle.value)
    }

    companion object {
        @Suppress("unused")
        internal val TAG = EglFlatProgram::class.java.simpleName

        private const val VERTEX_SHADER =
                "uniform mat4 uMVPMatrix;\n" +
                        "attribute vec4 aPosition;\n" +
                        "void main() {\n" +
                        "    gl_Position = uMVPMatrix * aPosition;\n" +
                        "}\n"

        private const val FRAGMENT_SHADER =
                "precision mediump float;\n" +
                        "uniform vec4 uColor;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = uColor;\n" +
                        "}\n"
    }
}