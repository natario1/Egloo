package com.otaliastudios.opengl.program


import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.draw.GlDrawable

/**
 * Base class for a program that accepts a vertex and a fragment shader in the constructor.
 * The program will be created automatically and released when [release] is called.
 *
 * Subclasses are required to do two things - typically, during the [onPreDraw] callback:
 * 1 Inspect the [GlDrawable] properties:
 *   - [GlDrawable.vertexArray]
 *   - [GlDrawable.coordsPerVertex]
 *   - [GlDrawable.vertexStride]
 *   These should be passed to the vertex shader.
 * 2 Pass the MVP matrix to the vertex shader as well.
 *
 * The vertex shader should then use the two to compute the gl_Position.
 */
abstract class GlProgram protected constructor(
        val handle: Int,
        private val ownsHandle: Boolean) {

    constructor(vertexShader: String, fragmentShader: String)
            : this(create(vertexShader, fragmentShader), true)

    constructor(handle: Int)
            : this(handle, false)

    private var isReleased = false

    @Suppress("unused")
    open fun release() {
        if (!isReleased && ownsHandle) {
            GLES20.glDeleteProgram(handle)
            isReleased = true
        }
    }

    @JvmOverloads
    fun draw(drawable: GlDrawable,
             modelViewProjectionMatrix: FloatArray = drawable.modelMatrix) {
        Egloo.checkGlError("draw start")
        GLES20.glUseProgram(handle)
        Egloo.checkGlError("glUseProgram")

        onPreDraw(drawable, modelViewProjectionMatrix)
        onDraw(drawable)
        onPostDraw(drawable)

        GLES20.glUseProgram(0)
        Egloo.checkGlError("draw end")
    }

    open fun onPreDraw(drawable: GlDrawable, modelViewProjectionMatrix: FloatArray) {}

    open fun onDraw(drawable: GlDrawable) {
        drawable.draw()
    }

    open fun onPostDraw(drawable: GlDrawable) {}

    protected fun getAttribHandle(name: String) = GlProgramLocation.getAttrib(handle, name)

    protected fun getUniformHandle(name: String) = GlProgramLocation.getUniform(handle, name)

    companion object {
        @Suppress("unused")
        internal val TAG = GlProgram::class.java.simpleName

        // Compiles the given shader, returns a handle.
        private fun createShader(shaderType: Int, source: String): Int {
            val shader = GLES20.glCreateShader(shaderType)
            Egloo.checkGlError("glCreateShader type=$shaderType")
            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                val message = "Could not compile shader $shaderType: ${GLES20.glGetShaderInfoLog(shader)} source: $source"
                GLES20.glDeleteShader(shader)
                throw RuntimeException(message)
            }
            return shader
        }

        @JvmStatic
        fun create(vertexShaderSource: String, fragmentShaderSource: String): Int {
            val pixelShader = createShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource)
            if (pixelShader == 0) throw RuntimeException("Could not load fragment shader")
            val vertexShader = createShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource)
            if (vertexShader == 0) throw RuntimeException("Could not load vertex shader")

            val program = GLES20.glCreateProgram()
            Egloo.checkGlError("glCreateProgram")
            if (program == 0) {
                throw RuntimeException("Could not create program")
            }
            GLES20.glAttachShader(program, vertexShader)
            Egloo.checkGlError("glAttachShader")
            GLES20.glAttachShader(program, pixelShader)
            Egloo.checkGlError("glAttachShader")
            GLES20.glLinkProgram(program)
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GLES20.GL_TRUE) {
                val message = "Could not link program: " + GLES20.glGetProgramInfoLog(program)
                GLES20.glDeleteProgram(program)
                throw RuntimeException(message)
            }
            return program
        }
    }
}