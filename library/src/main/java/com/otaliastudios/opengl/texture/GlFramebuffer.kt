package com.otaliastudios.opengl.texture

import android.opengl.GLES20
import com.otaliastudios.opengl.core.GlBindable
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.core.use

open class GlFramebuffer(id: Int? = null) : GlBindable {

    val id = id ?: run {
        val array = IntArray(1)
        GLES20.glGenFramebuffers(1, array, 0)
        Egloo.checkGlError("glGenFramebuffers")
        array[0]
    }

    @JvmOverloads
    fun attach(texture: GlTexture, attachment: Int = GLES20.GL_COLOR_ATTACHMENT0) {
        use {
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                    attachment,
                    texture.target,
                    texture.id,
                    0)
            val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
            if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                throw RuntimeException("Invalid framebuffer generation. Error:$status")
            }
        }
    }

    override fun bind() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id)
    }

    override fun unbind() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    fun release() {
        GLES20.glDeleteFramebuffers(1, intArrayOf(id), 0)
    }
}