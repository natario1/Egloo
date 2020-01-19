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
 * See [GlTextureProgram] for the simplest implementation.
 */
@Suppress("unused")
open class GlBaseTextureProgram constructor(
        vertexShader: String,
        fragmentShader: String,
        private val textureUnit: Int,
        private val textureTarget: Int
) : GlProgram(vertexShader, fragmentShader) {

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

    override fun onPreDraw(drawable: GlDrawable, modelViewProjectionMatrix: FloatArray) {
        super.onPreDraw(drawable, modelViewProjectionMatrix)
        GLES20.glActiveTexture(textureUnit)
        GLES20.glBindTexture(textureTarget, textureId)
    }

    override fun onPostDraw(drawable: GlDrawable) {
        super.onPostDraw(drawable)
        GLES20.glBindTexture(textureTarget, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        Egloo.checkGlError("onPostDraw end")
    }

    companion object {
        @Suppress("unused")
        internal val TAG = GlBaseTextureProgram::class.java.simpleName
    }

}