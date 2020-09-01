@file:Suppress("unused")

package com.otaliastudios.opengl.buffer

import com.otaliastudios.opengl.core.GlBindable
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.internal.glBindBuffer
import com.otaliastudios.opengl.internal.glDeleteBuffers
import com.otaliastudios.opengl.internal.glGenBuffers

public open class GlBuffer(public val target: Int, id: Int? = null) : GlBindable {

    public val id: Int = id ?: run {
        val array = UIntArray(1)
        glGenBuffers(1, array)
        Egloo.checkGlError("glGenBuffers")
        array[0].toInt()
    }

    override fun bind() {
        glBindBuffer(target.toUInt(), id.toUInt())
    }

    override fun unbind() {
        glBindBuffer(target.toUInt(), 0U)
    }

    public fun release() {
        glDeleteBuffers(1, uintArrayOf(id.toUInt()))
    }
}