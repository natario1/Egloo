package com.otaliastudios.opengl.program


import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.draw.GlDrawable
import com.otaliastudios.opengl.texture.GlTexture

/**
 * Base implementation for a [GlProgram] that draws textures.
 * See [GlTextureProgram] for the simplest implementation.
 */
@Suppress("unused")
open class GlBaseTextureProgram constructor(
        vertexShader: String,
        fragmentShader: String,
        private var texture: GlTexture
) : GlProgram(vertexShader, fragmentShader) {

    val textureHandle get() = texture.handle

    override fun onPreDraw(drawable: GlDrawable, modelViewProjectionMatrix: FloatArray) {
        super.onPreDraw(drawable, modelViewProjectionMatrix)
        GLES20.glActiveTexture(texture.unit)
        GLES20.glBindTexture(texture.target, texture.handle)
    }

    override fun onPostDraw(drawable: GlDrawable) {
        super.onPostDraw(drawable)
        GLES20.glBindTexture(texture.target, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        Egloo.checkGlError("onPostDraw end")
    }

    companion object {
        @Suppress("unused")
        internal val TAG = GlBaseTextureProgram::class.java.simpleName
    }

}