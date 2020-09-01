@file:Suppress("unused")

package com.otaliastudios.opengl.texture

import com.otaliastudios.opengl.core.GlBindable
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.core.use
import com.otaliastudios.opengl.internal.*
import kotlin.jvm.JvmOverloads

public class GlFramebuffer(id: Int? = null) : GlBindable {

    public val id: Int = id ?: run {
        val array = UIntArray(1)
        glGenFramebuffers(1, array)
        Egloo.checkGlError("glGenFramebuffers")
        array[0].toInt()
    }

    @JvmOverloads
    public fun attach(texture: GlTexture, attachment: Int = GL_COLOR_ATTACHMENT0.toInt()) {
        use {
            glFramebufferTexture2D(GL_FRAMEBUFFER, attachment.toUInt(),
                    texture.target.toUInt(), texture.id.toUInt(), 0)
            val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
            if (status != GL_FRAMEBUFFER_COMPLETE) {
                throw RuntimeException("Invalid framebuffer generation. Error:$status")
            }
        }
    }

    override fun bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id.toUInt())
    }

    override fun unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0u)
    }

    public fun release() {
        glDeleteFramebuffers(1, uintArrayOf(id.toUInt()))
    }
}