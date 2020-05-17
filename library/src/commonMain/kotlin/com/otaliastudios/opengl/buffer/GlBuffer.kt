@file:Suppress("unused", "EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.otaliastudios.opengl.buffer

import com.otaliastudios.opengl.core.GlBindable
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.internal.glBindBuffer
import com.otaliastudios.opengl.internal.glDeleteBuffers
import com.otaliastudios.opengl.internal.glGenBuffers

open class GlBuffer(val target: Int, id: Int? = null) : GlBindable {

    val id = id ?: run {
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

    fun release() {
        glDeleteBuffers(1, uintArrayOf(id.toUInt()))
    }
}