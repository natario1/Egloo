package com.otaliastudios.opengl.texture

import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo

class GlTexture(val unit: Int, val target: Int) {

    val handle = run {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        Egloo.checkGlError("glGenTextures")
        val textureId = textures[0]

        GLES20.glActiveTexture(unit)
        GLES20.glBindTexture(target, textureId)
        Egloo.checkGlError("glBindTexture $textureId")

        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        Egloo.checkGlError("glTexParameter")

        GLES20.glBindTexture(target, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        Egloo.checkGlError("init end")
        textureId
    }
}