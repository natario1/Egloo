package com.otaliastudios.opengl.buffer

import android.opengl.GLES31
import androidx.annotation.RequiresApi
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.core.use

@RequiresApi(21)
class GlShaderStorageBuffer(val size: Int, usage: Int)
    : GlBuffer(target = GLES31.GL_SHADER_STORAGE_BUFFER) {

    init {
        use {
            GLES31.glBufferData(target, size, null, usage)
            Egloo.checkGlError("glBufferData")
        }
    }

    fun bind(index: Int) {
        // Note: a third option, glBindBufferRange, will only bind a subrange of the SSBO.
        // https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glBindBuffer.xhtml
        // https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glBindBufferBase.xhtml
        // https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glBindBufferRange.xhtml
        GLES31.glBindBufferBase(target, index, id)
        Egloo.checkGlError("glBindBufferBase")
    }

    // Can create an interface like GlBindable for indexed targets like GL_SHADER_STORAGE_BUFFER
    fun use(index: Int, block: () -> Unit) {
        bind(index)
        block()
        unbind()
    }
}