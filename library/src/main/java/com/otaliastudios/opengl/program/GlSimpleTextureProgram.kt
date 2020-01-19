package com.otaliastudios.opengl.program


/**
 * An [GlTextureProgram] that uses a simple vertex shader and a texture fragment shader.
 */
@Suppress("unused")
open class GlSimpleTextureProgram : GlTextureProgram(
        SIMPLE_VERTEX_SHADER,
        SIMPLE_FRAGMENT_SHADER) {

    companion object {
        @Suppress("unused")
        internal val TAG = GlSimpleTextureProgram::class.java.simpleName
    }
}