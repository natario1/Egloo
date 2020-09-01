package com.otaliastudios.opengl.program

import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.internal.glGetAttribLocation
import com.otaliastudios.opengl.internal.glGetUniformLocation

/**
 * A simple helper class for holding handles to program variables.
 */
public class GlProgramLocation private constructor(
        program: Int,
        type: Type,
        @Suppress("CanBeParameter") public val name: String
) {

    private enum class Type { ATTRIB, UNIFORM }

    public val value: Int
    init {
        value = when (type) {
            Type.ATTRIB -> glGetAttribLocation(program.toUInt(), name)
            Type.UNIFORM -> glGetUniformLocation(program.toUInt(), name)
        }
        Egloo.checkGlProgramLocation(value, name)
    }

    internal val uvalue = value.toUInt()

    public companion object {
        public fun getAttrib(program: Int, name: String): GlProgramLocation = GlProgramLocation(program, Type.ATTRIB, name)
        public fun getUniform(program: Int, name: String): GlProgramLocation = GlProgramLocation(program, Type.UNIFORM, name)
    }
}