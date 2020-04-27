package com.otaliastudios.opengl.program

import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo

class GlShader(val type: Int, val id: Int) {

    constructor(type: Int, source: String) : this(type, compile(type, source))

    fun release() {
        GLES20.glDeleteShader(id)
    }

    companion object {
        private fun compile(type: Int, source: String): Int {
            val shader = GLES20.glCreateShader(type)
            Egloo.checkGlError("glCreateShader type=$type")
            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                val message = "Could not compile shader $type: ${GLES20.glGetShaderInfoLog(shader)} source: $source"
                GLES20.glDeleteShader(shader)
                throw RuntimeException(message)
            }
            return shader
        }
    }
}