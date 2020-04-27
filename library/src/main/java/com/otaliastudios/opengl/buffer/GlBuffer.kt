@file:Suppress("unused")

package com.otaliastudios.opengl.buffer

import android.opengl.GLES20
import com.otaliastudios.opengl.core.GlBindable
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.core.use

open class GlBuffer(val target: Int, id: Int? = null) : GlBindable {

    val id = id ?: run {
        val array = IntArray(1)
        GLES20.glGenBuffers(1, array, 0)
        Egloo.checkGlError("glGenBuffers")
        array[0]
    }

    override fun bind() {
        GLES20.glBindBuffer(target, id)
    }

    override fun unbind() {
        GLES20.glBindBuffer(target, 0)
    }

    fun release() {
        GLES20.glDeleteBuffers(1, intArrayOf(id), 0)
    }
}