package com.otaliastudios.opengl.texture

import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.otaliastudios.opengl.core.GlBindable
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.core.use

class GlTexture private constructor(
        val unit: Int,
        val target: Int,
        id: Int?,
        width: Int?,
        height: Int?,
        format: Int?) : GlBindable {

    @JvmOverloads
    constructor(unit: Int = GLES20.GL_TEXTURE0, target: Int = GLES11Ext.GL_TEXTURE_EXTERNAL_OES, id: Int? = null)
            : this(unit, target, id, null, null, null)

    @JvmOverloads
    constructor(unit: Int, target: Int, width: Int, height: Int, format: Int = GLES20.GL_RGBA)
            : this(unit, target, null, width, height, format)

    val id = id ?: run {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        Egloo.checkGlError("glGenTextures")
        textures[0]
    }

    init {
        if (id == null) {
            use {
                if (width != null && height != null && format != null) {
                    GLES20.glTexImage2D(target, 0,
                            format, width, height, 0,
                            format, GLES20.GL_UNSIGNED_BYTE, null)
                }
                GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
                GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
                GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
                GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
                Egloo.checkGlError("glTexParameter")
            }
        }
    }

    override fun bind() {
        GLES20.glActiveTexture(unit)
        GLES20.glBindTexture(target, id)
        Egloo.checkGlError("bind")
    }

    override fun unbind() {
        GLES20.glBindTexture(target, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        Egloo.checkGlError("unbind")
    }

    fun release() {
        GLES20.glDeleteTextures(1, intArrayOf(id), 0)
    }
}