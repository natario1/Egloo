package com.otaliastudios.opengl.program


import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egl
import java.nio.FloatBuffer

/**
 * An [EglProgram] that uses a simple vertex shader and a texture fragment shader.
 * This means that the fragment shader draws texture2D elements.
 *
 * Internally this uses [GLES20.glBindTexture] and [GLES11Ext.GL_TEXTURE_EXTERNAL_OES].
 * The texture ID is passed outside so the callers can draw on that texture ID and then
 * call draw() here.
 */
open class EglTextureProgram : EglProgram() {

    companion object {
        internal val TAG = EglTextureProgram::class.java.simpleName

        // Simple vertex shader.
        private val SIMPLE_VERTEX_SHADER =
                "uniform mat4 uMVPMatrix;\n" +
                        "uniform mat4 uTexMatrix;\n" +
                        "attribute vec4 aPosition;\n" +
                        "attribute vec4 aTextureCoord;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "void main() {\n" +
                        "    gl_Position = uMVPMatrix * aPosition;\n" +
                        "    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                        "}\n"

        // Simple fragment shader for use with external 2D textures
        private val SIMPLE_FRAGMENT_SHADER =
                "#extension GL_OES_EGL_image_external : require\n" +
                        "precision mediump float;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "uniform samplerExternalOES sTexture;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                        "}\n"

        private const val TEXTURE_TARGET = GLES11Ext.GL_TEXTURE_EXTERNAL_OES
    }

    // Stuff from Texture2dProgram
    private var programHandle = createProgram(SIMPLE_VERTEX_SHADER, SIMPLE_FRAGMENT_SHADER)
    init {
        if (programHandle == 0) {
            throw RuntimeException("Could not create program.")
        }
    }

    private val aPositionLocation: Int
    private val aTextureCoordLocation: Int
    private val uMVPMatrixLocation: Int
    private val uTexMatrixLocation: Int
    init {
        aPositionLocation = GLES20.glGetAttribLocation(programHandle, "aPosition")
        Egl.checkLocation(aPositionLocation, "aPosition")
        aTextureCoordLocation = GLES20.glGetAttribLocation(programHandle, "aTextureCoord")
        Egl.checkLocation(aTextureCoordLocation, "aTextureCoord")
        uMVPMatrixLocation = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix")
        Egl.checkLocation(uMVPMatrixLocation, "uMVPMatrix")
        uTexMatrixLocation = GLES20.glGetUniformLocation(programHandle, "uTexMatrix")
        Egl.checkLocation(uTexMatrixLocation, "uTexMatrix")
    }

    @JvmOverloads
    fun release(doEglCleanup: Boolean = true) {
        if (doEglCleanup) GLES20.glDeleteProgram(programHandle)
        programHandle = -1
    }

    fun createTexture(): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        Egl.check("glGenTextures")

        val texId = textures[0]
        GLES20.glBindTexture(TEXTURE_TARGET, texId)
        Egl.check("glBindTexture $texId")

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        Egl.check("glTexParameter")

        return texId
    }

    fun draw(mvpMatrix: FloatArray, textureId: Int, textureMatrix: FloatArray,
             vertexBuffer: FloatBuffer, firstVertex: Int, vertexCount: Int, vertexStride: Int,
             coordsPerVertex: Int,
             texCoordBuffer: FloatBuffer, texCoordStride: Int) {
        Egl.check("draw start")

        // Select the program.
        GLES20.glUseProgram(programHandle)
        Egl.check("glUseProgram")

        // Set the texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(TEXTURE_TARGET, textureId)

        // Copy the texture transformation matrix over.
        GLES20.glUniformMatrix4fv(uTexMatrixLocation, 1, false, textureMatrix, 0)
        Egl.check("glUniformMatrix4fv")

        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0)
        Egl.check("glUniformMatrix4fv")

        // Enable the "aPosition" vertex attribute.
        // Connect vertexBuffer to "aPosition".
        GLES20.glEnableVertexAttribArray(aPositionLocation)
        Egl.check("glEnableVertexAttribArray")
        GLES20.glVertexAttribPointer(aPositionLocation, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)
        Egl.check("glVertexAttribPointer")

        // Enable the "aTextureCoord" vertex attribute.
        // Connect texBuffer to "aTextureCoord".
        GLES20.glEnableVertexAttribArray(aTextureCoordLocation)
        Egl.check("glEnableVertexAttribArray")
        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, texCoordStride, texCoordBuffer)
        Egl.check("glVertexAttribPointer")


        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount)
        Egl.check("glDrawArrays")

        // Done -- disable vertex array, texture, and program.
        GLES20.glDisableVertexAttribArray(aPositionLocation)
        GLES20.glDisableVertexAttribArray(aTextureCoordLocation)
        GLES20.glBindTexture(TEXTURE_TARGET, 0)
        GLES20.glUseProgram(0)
    }
}