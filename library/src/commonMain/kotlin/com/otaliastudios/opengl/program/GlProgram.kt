package com.otaliastudios.opengl.program

import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.core.GlBindable
import com.otaliastudios.opengl.core.use
import com.otaliastudios.opengl.draw.GlDrawable
import com.otaliastudios.opengl.internal.*
import com.otaliastudios.opengl.internal.GL_FRAGMENT_SHADER
import com.otaliastudios.opengl.internal.GL_VERTEX_SHADER
import com.otaliastudios.opengl.internal.glAttachShader
import com.otaliastudios.opengl.internal.glCreateProgram
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

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
public open class GlProgram protected constructor(
        public val handle: Int,
        private val ownsHandle: Boolean,
        private vararg val shaders: GlShader) : GlBindable {

    @Suppress("unused")
    public constructor(handle: Int) : this(handle, false)

    public constructor(vertexShader: String, fragmentShader: String) : this(
            GlShader(GL_VERTEX_SHADER.toInt(), vertexShader),
            GlShader(GL_FRAGMENT_SHADER.toInt(), fragmentShader))

    public constructor(vararg shaders: GlShader)
            : this(create(*shaders), true, *shaders)

    private var isReleased = false

    @Suppress("unused")
    public open fun release() {
        if (!isReleased) {
            if (ownsHandle) glDeleteProgram(handle.toUInt())
            shaders.forEach { it.release() }
            isReleased = true
        }
    }

    override fun bind() {
        glUseProgram(handle.toUInt())
        Egloo.checkGlError("glUseProgram")
    }

    override fun unbind() {
        glUseProgram(0u)
    }

    // TODO move draw API to GlScene or somewhere else.
    //  I like the program as an object that manages the single shaders capabilities,
    //  but not quite as the drawer element. It could be a compute program for instance.
    @JvmOverloads
    public fun draw(drawable: GlDrawable,
                    modelViewProjectionMatrix: FloatArray = drawable.modelMatrix) {
        Egloo.checkGlError("draw start")
        use {
            onPreDraw(drawable, modelViewProjectionMatrix)
            onDraw(drawable)
            onPostDraw(drawable)
        }
        Egloo.checkGlError("draw end")
    }

    public open fun onPreDraw(drawable: GlDrawable, modelViewProjectionMatrix: FloatArray) {}

    public open fun onDraw(drawable: GlDrawable) {
        drawable.draw()
    }

    public open fun onPostDraw(drawable: GlDrawable) {}

    protected fun getAttribHandle(name: String): GlProgramLocation = GlProgramLocation.getAttrib(handle, name)

    protected fun getUniformHandle(name: String): GlProgramLocation = GlProgramLocation.getUniform(handle, name)

    public companion object {

        @Deprecated(message = "Use create(GlShader) signature.")
        @JvmStatic
        public fun create(vertexShaderSource: String, fragmentShaderSource: String): Int {
            return create(GlShader(GL_VERTEX_SHADER.toInt(), vertexShaderSource),
                    GlShader(GL_FRAGMENT_SHADER.toInt(), fragmentShaderSource))
        }

        @JvmStatic
        public fun create(vararg shaders: GlShader): Int {
            val program = glCreateProgram()
            Egloo.checkGlError("glCreateProgram")
            if (program == 0u) {
                throw RuntimeException("Could not create program")
            }
            shaders.forEach {
                glAttachShader(program, it.id.toUInt())
                Egloo.checkGlError("glAttachShader")
            }
            glLinkProgram(program)
            val linkStatus = IntArray(1)
            glGetProgramiv(program, GL_LINK_STATUS, linkStatus)
            if (linkStatus[0] != GL_TRUE) {
                val message = "Could not link program: " + glGetProgramInfoLog(program)
                glDeleteProgram(program)
                throw RuntimeException(message)
            }
            return program.toInt()
        }
    }
}