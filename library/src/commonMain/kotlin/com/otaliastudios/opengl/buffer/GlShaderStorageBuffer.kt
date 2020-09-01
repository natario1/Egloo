package com.otaliastudios.opengl.buffer

import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.core.use
import com.otaliastudios.opengl.internal.GL_SHADER_STORAGE_BUFFER
import com.otaliastudios.opengl.internal.glBindBufferBase
import com.otaliastudios.opengl.internal.glBufferData

// TODO android must have requiresApi annotation (21)
@Suppress("unused")
class GlShaderStorageBuffer(val size: Int, usage: Int)
    : GlBuffer(target = GL_SHADER_STORAGE_BUFFER.toInt()) {

    init {
        use {
            glBufferData(target.toUInt(), size, usage.toUInt())
            Egloo.checkGlError("glBufferData")
        }
    }

    fun bind(index: Int) {
        // Note: a third option, glBindBufferRange, will only bind a subrange of the SSBO.
        // https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glBindBuffer.xhtml
        // https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glBindBufferBase.xhtml
        // https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glBindBufferRange.xhtml
        glBindBufferBase(target.toUInt(), index.toUInt(), id.toUInt())
        Egloo.checkGlError("glBindBufferBase")
    }

    // Can create an interface like GlBindable for indexed targets like GL_SHADER_STORAGE_BUFFER
    fun use(index: Int, block: () -> Unit) {
        bind(index)
        block()
        unbind()
    }
}