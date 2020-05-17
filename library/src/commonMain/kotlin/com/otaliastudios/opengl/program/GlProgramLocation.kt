@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.otaliastudios.opengl.program

import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.internal.glGetAttribLocation
import com.otaliastudios.opengl.internal.glGetUniformLocation

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
            Type.ATTRIB -> glGetAttribLocation(program.toUInt(), name)
            Type.UNIFORM -> glGetUniformLocation(program.toUInt(), name)
        }
        Egloo.checkGlProgramLocation(value, name)
    }

    internal val uvalue = value.toUInt()

    companion object {
        fun getAttrib(program: Int, name: String) = GlProgramLocation(program, Type.ATTRIB, name)
        fun getUniform(program: Int, name: String) = GlProgramLocation(program, Type.UNIFORM, name)
    }
}