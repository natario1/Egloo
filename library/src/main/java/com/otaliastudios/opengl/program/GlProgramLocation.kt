package com.otaliastudios.opengl.program

import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo

/**
 * A simple helper class for holding handles to program variables.
 */
class GlProgramLocation private constructor(
        program: Int,
        type: Type,
        @Suppress("CanBeParameter") val name: String
) {

    private enum class Type { ATTRIB, UNIFORM }

    val value: Int
    init {
        value = when (type) {
            Type.ATTRIB -> GLES20.glGetAttribLocation(program, name)
            Type.UNIFORM -> GLES20.glGetUniformLocation(program, name)
        }
        Egloo.checkGlProgramLocation(value, name)
    }

    companion object {
        fun getAttrib(program: Int, name: String) = GlProgramLocation(program, Type.ATTRIB, name)
        fun getUniform(program: Int, name: String) = GlProgramLocation(program, Type.UNIFORM, name)
    }
}