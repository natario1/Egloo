package com.otaliastudios.opengl.program


import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egl
import java.nio.FloatBuffer

/**
 * An [EglProgram] that uses basic flat-shading rendering,
 * based on FlatShadedProgram from grafika.
 */
open class EglFlatProgram : EglProgram() {

    companion object {
        internal val TAG = EglFlatProgram::class.java.simpleName

        private val VERTEX_SHADER =
                "uniform mat4 uMVPMatrix;\n" +
                        "attribute vec4 aPosition;\n" +
                        "void main() {\n" +
                        "    gl_Position = uMVPMatrix * aPosition;\n" +
                        "}\n"

        private val FRAGMENT_SHADER =
                "precision mediump float;\n" +
                        "uniform vec4 uColor;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = uColor;\n" +
                        "}\n"
    }

    private var programHandle = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
    init {
        if (programHandle == 0) {
            throw RuntimeException("Could not create program.")
        }
    }

    private val aPositionLocation: Int
    private val uMVPMatrixLocation: Int
    private val uColorLocation: Int
    init {
        aPositionLocation = GLES20.glGetAttribLocation(programHandle, "aPosition")
        Egl.checkLocation(aPositionLocation, "aPosition")
        uMVPMatrixLocation = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix")
        Egl.checkLocation(uMVPMatrixLocation, "uMVPMatrix")
        uColorLocation = GLES20.glGetUniformLocation(programHandle, "uColor")
        Egl.checkLocation(uColorLocation, "uColor")
    }

    @JvmOverloads
    fun release(doEglCleanup: Boolean = true) {
        if (doEglCleanup) GLES20.glDeleteProgram(programHandle)
        programHandle = -1
    }

    /**
     * @param color A 4-element color vector.
     * @param mvpMatrix The 4x4 projection matrix.
     * @param vertexBuffer Buffer with vertex data.
     * @param firstVertex Index of first vertex to use in vertexBuffer.
     * @param vertexCount Number of vertices in vertexBuffer.
     * @param coordsPerVertex The number of coordinates per vertex (e.g. x,y is 2).
     * @param vertexStride Width, in bytes, of the data for each vertex (often vertexCount * sizeof(float)).
     */
    fun draw(mvpMatrix: FloatArray, color: FloatArray,
                     vertexBuffer: FloatBuffer, firstVertex: Int,
                     vertexCount: Int, vertexStride: Int, coordsPerVertex: Int) {
        Egl.check("draw start")

        // Select the program.
        GLES20.glUseProgram(programHandle)
        Egl.check("glUseProgram")

        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0)
        Egl.check("glUniformMatrix4fv")

        // Copy the color vector in.
        GLES20.glUniform4fv(uColorLocation, 1, color, 0)
        Egl.check("glUniform4fv")

        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(aPositionLocation)
        Egl.check("glEnableVertexAttribArray")

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(aPositionLocation, coordsPerVertex,
                GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)
        Egl.check("glVertexAttribPointer")

        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount)
        Egl.check("glDrawArrays")

        // Done -- disable vertex array and program.
        GLES20.glDisableVertexAttribArray(aPositionLocation)
        GLES20.glUseProgram(0)
    }

}