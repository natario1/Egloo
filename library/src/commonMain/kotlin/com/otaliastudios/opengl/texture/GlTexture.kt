package com.otaliastudios.opengl.texture

import com.otaliastudios.opengl.core.GlBindable
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.core.use
import com.otaliastudios.opengl.internal.*
import kotlin.jvm.JvmOverloads

class GlTexture private constructor(
        val unit: Int,
        val target: Int,
        id: Int?,
        width: Int?,
        height: Int?,
        format: Int?,
        internalFormat: Int?,
        type: Int?) : GlBindable {

    @JvmOverloads
    constructor(unit: Int = GL_TEXTURE0.toInt(), target: Int = GL_TEXTURE_EXTERNAL_OES.toInt(), id: Int? = null)
            : this(unit, target, id, null, null, null, null, null)

    @JvmOverloads
    constructor(unit: Int, target: Int, width: Int, height: Int,
                format: Int = GL_RGBA.toInt(),
                internalFormat: Int = format,
                type: Int = GL_UNSIGNED_BYTE.toInt())
            : this(unit, target, null, width, height, format, internalFormat, type)

    val id = id ?: run {
        val textures = UIntArray(1)
        glGenTextures(1, textures)
        Egloo.checkGlError("glGenTextures")
        textures[0].toInt()
    }

    init {
        if (id == null) {
            use {
                if (width != null && height != null
                        && format != null
                        && internalFormat != null
                        && type != null) {
                    glTexImage2D(target.toUInt(), 0, internalFormat, width, height,
                            0, format.toUInt(), type.toUInt(), null)
                }
                glTexParameterf(target.toUInt(), GL_TEXTURE_MIN_FILTER, GL_NEAREST)
                glTexParameterf(target.toUInt(), GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                glTexParameteri(target.toUInt(), GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
                glTexParameteri(target.toUInt(), GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
                Egloo.checkGlError("glTexParameter")
            }
        }
    }

    override fun bind() {
        glActiveTexture(unit.toUInt())
        glBindTexture(target.toUInt(), id.toUInt())
        Egloo.checkGlError("bind")
    }

    override fun unbind() {
        glBindTexture(target.toUInt(), 0.toUInt())
        glActiveTexture(GL_TEXTURE0)
        Egloo.checkGlError("unbind")
    }

    fun release() {
        glDeleteTextures(1, uintArrayOf(id.toUInt()))
    }
}