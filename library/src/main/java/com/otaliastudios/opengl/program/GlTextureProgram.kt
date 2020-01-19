package com.otaliastudios.opengl.program


import android.graphics.RectF
import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.draw.Gl2dDrawable
import com.otaliastudios.opengl.draw.GlDrawable
import com.otaliastudios.opengl.extensions.floatBufferOf
import java.lang.RuntimeException

/**
 * Base implementation for a [GlProgram] that draws textures.
 * See [GlSimpleTextureProgram] for the simplest implementation.
 */
@Suppress("unused")
open class GlTextureProgram @JvmOverloads constructor(
        vertexShader: String,
        fragmentShader: String,
        private val textureUnit: Int = GLES20.GL_TEXTURE0,
        vertexPositionName: String = "aPosition",
        vertexMvpMatrixName: String = "uMVPMatrix",
        textureCoordsName: String = "aTextureCoord",
        textureTransformName: String = "uTexMatrix"
) : GlProgram(vertexShader, fragmentShader) {

    private val textureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES

    private val vertexPositionHandle = getAttribHandle(vertexPositionName)
    private val vertexMvpMatrixHandle = getUniformHandle(vertexMvpMatrixName)
    private val textureCoordsHandle = getAttribHandle(textureCoordsName)
    private val textureTransformHandle = getUniformHandle(textureTransformName)

    private val drawableBounds = RectF()
    private var textureCoordsBuffer = floatBufferOf(8)

    private fun ensureTextureCoordsBuffer(size: Int) {
        if (textureCoordsBuffer.capacity() < size) {
            textureCoordsBuffer = floatBufferOf(size)
        }
        textureCoordsBuffer.clear()
        textureCoordsBuffer.limit(size)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val textureId: Int
    init {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        Egloo.checkGlError("glGenTextures")
        textureId = textures[0]

        GLES20.glActiveTexture(textureUnit)
        GLES20.glBindTexture(textureTarget, textureId)
        Egloo.checkGlError("glBindTexture $textureId")

        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        Egloo.checkGlError("glTexParameter")

        GLES20.glBindTexture(textureTarget, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        Egloo.checkGlError("init end")
    }

    @Suppress("MemberVisibilityCanBePrivate")
    var textureTransform: FloatArray = Egloo.IDENTITY_MATRIX.clone()

    override fun onPreDraw(drawable: GlDrawable, modelViewProjectionMatrix: FloatArray) {
        super.onPreDraw(drawable, modelViewProjectionMatrix)
        GLES20.glActiveTexture(textureUnit)
        GLES20.glBindTexture(textureTarget, textureId)

        // Copy the modelViewProjectionMatrix over.
        GLES20.glUniformMatrix4fv(vertexMvpMatrixHandle.value, 1, false,
                modelViewProjectionMatrix, 0)
        Egloo.checkGlError("glUniformMatrix4fv")

        // Copy the texture transformation matrix over.
        GLES20.glUniformMatrix4fv(textureTransformHandle.value, 1, false,
                textureTransform, 0)
        Egloo.checkGlError("glUniformMatrix4fv")

        // Enable the "aPosition" vertex attribute.
        // Connect vertexBuffer to "aPosition".
        if (drawable !is Gl2dDrawable) {
            throw RuntimeException("GlTextureProgram only supports 2D drawables.")
        }
        val vertexStride = 2 * Egloo.SIZE_OF_FLOAT
        GLES20.glEnableVertexAttribArray(vertexPositionHandle.value)
        Egloo.checkGlError("glEnableVertexAttribArray")
        GLES20.glVertexAttribPointer(vertexPositionHandle.value, 2,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                drawable.vertexArray)
        Egloo.checkGlError("glVertexAttribPointer")

        // We must compute the texture coordinates given the drawable vertex array.
        // To do this, we ask the drawable for its boundaries, then apply the texture
        // onto this rect.
        // TODO cache so that we only do this if drawable bounds have changed.
        drawable.getBounds(drawableBounds)
        val coordinates = drawable.vertexCount * 2
        ensureTextureCoordsBuffer(coordinates)
        for (i in 0 until coordinates) {
            val isX = i % 2 == 0
            val drawableValue = drawable.vertexArray.get(i)
            val drawableMinValue = if (isX) drawableBounds.left else drawableBounds.bottom
            val drawableMaxValue = if (isX) drawableBounds.right else drawableBounds.top
            val drawableFraction = (drawableValue - drawableMinValue) / (drawableMaxValue - drawableMinValue)
            val textureValue = 0F + drawableFraction * 1F // tex value goes from 0 to 1
            textureCoordsBuffer.put(i, textureValue)
        }

        GLES20.glEnableVertexAttribArray(textureCoordsHandle.value)
        Egloo.checkGlError("glEnableVertexAttribArray")
        GLES20.glVertexAttribPointer(textureCoordsHandle.value, 2,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                textureCoordsBuffer)
        Egloo.checkGlError("glVertexAttribPointer")
    }

    override fun onPostDraw(drawable: GlDrawable) {
        super.onPostDraw(drawable)
        GLES20.glDisableVertexAttribArray(vertexPositionHandle.value)
        GLES20.glDisableVertexAttribArray(textureCoordsHandle.value)
        GLES20.glBindTexture(textureTarget, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        Egloo.checkGlError("onPostDraw end")
    }

    companion object {
        @Suppress("unused")
        internal val TAG = GlTextureProgram::class.java.simpleName

        const val SIMPLE_VERTEX_SHADER =
                "" +
                        "uniform mat4 uMVPMatrix;\n" +
                        "uniform mat4 uTexMatrix;\n" +
                        "attribute vec4 aPosition;\n" +
                        "attribute vec4 aTextureCoord;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "void main() {\n" +
                        "    gl_Position = uMVPMatrix * aPosition;\n" +
                        "    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                        "}\n"

        const val SIMPLE_FRAGMENT_SHADER =
                "" +
                        "#extension GL_OES_EGL_image_external : require\n" +
                        "precision mediump float;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "uniform samplerExternalOES sTexture;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                        "}\n"
    }

}