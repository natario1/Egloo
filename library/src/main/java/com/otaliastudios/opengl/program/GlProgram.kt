package com.otaliastudios.opengl.program


import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.core.GlBindable
import com.otaliastudios.opengl.core.use
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
        private val ownsHandle: Boolean,
        private vararg val shaders: GlShader) : GlBindable {

    constructor(handle: Int) : this(handle, false)

    constructor(vertexShader: String, fragmentShader: String) : this(
            GlShader(GLES20.GL_VERTEX_SHADER, vertexShader),
            GlShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader))

    constructor(vararg shaders: GlShader)
            : this(create(*shaders), true, *shaders)

    private var isReleased = false

    @Suppress("unused")
    open fun release() {
        if (!isReleased) {
            if (ownsHandle) GLES20.glDeleteProgram(handle)
            shaders.forEach { it.release() }
            isReleased = true
        }
    }

    override fun bind() {
        GLES20.glUseProgram(handle)
        Egloo.checkGlError("glUseProgram")
    }

    override fun unbind() {
        GLES20.glUseProgram(0)
    }

    // TODO move draw API to GlScene or somewhere else.
    //  I like the program as an object that manages the single shaders capabilities,
    //  but not quite as the drawer element. It could be a compute program for instance.
    @JvmOverloads
    fun draw(drawable: GlDrawable,
             modelViewProjectionMatrix: FloatArray = drawable.modelMatrix) {
        Egloo.checkGlError("draw start")
        use {
            onPreDraw(drawable, modelViewProjectionMatrix)
            onDraw(drawable)
            onPostDraw(drawable)
        }
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

        @Deprecated(message = "Use create(GlShader) signature.")
        @JvmStatic
        fun create(vertexShaderSource: String, fragmentShaderSource: String): Int {
            return create(GlShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource),
                    GlShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource))
        }

        @JvmStatic
        fun create(vararg shaders: GlShader): Int {
            val program = GLES20.glCreateProgram()
            Egloo.checkGlError("glCreateProgram")
            if (program == 0) {
                throw RuntimeException("Could not create program")
            }
            shaders.forEach {
                GLES20.glAttachShader(program, it.id)
                Egloo.checkGlError("glAttachShader")
            }
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