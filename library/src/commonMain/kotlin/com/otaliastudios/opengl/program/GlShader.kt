package com.otaliastudios.opengl.program

import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.internal.*

class GlShader(val type: Int, val id: Int) {

    constructor(type: Int, source: String) : this(type, compile(type, source))

    fun release() {
        glDeleteShader(id.toUInt())
    }

    companion object {
        private fun compile(type: Int, source: String): Int {
            val shader = glCreateShader(type.toUInt())
            Egloo.checkGlError("glCreateShader type=$type")
            glShaderSource(shader, source)
            glCompileShader(shader)
            val compiled = IntArray(1)
            glGetShaderiv(shader, GL_COMPILE_STATUS, compiled)
            if (compiled[0] == 0) {
                val message = "Could not compile shader $type: '${glGetShaderInfoLog(shader)}' source: $source"
                glDeleteShader(shader)
                throw RuntimeException(message)
            }
            return shader.toInt()
        }
    }
}