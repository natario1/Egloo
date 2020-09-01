package com.otaliastudios.opengl.program


import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.draw.GlDrawable
import com.otaliastudios.opengl.internal.*
import com.otaliastudios.opengl.internal.GL_FLOAT
import com.otaliastudios.opengl.internal.glDisableVertexAttribArray
import com.otaliastudios.opengl.internal.glEnableVertexAttribArray
import com.otaliastudios.opengl.internal.glVertexAttribPointer

expect class GlFlatProgram : GlNativeFlatProgram

/**
 * An [GlProgram] that uses basic flat-shading rendering,
 * based on FlatShadedProgram from grafika.
 */
@Suppress("unused")
open class GlNativeFlatProgram internal constructor(): GlProgram(VERTEX_SHADER, FRAGMENT_SHADER) {

    private val vertexPositionHandle = getAttribHandle("aPosition")
    private val vertexMvpMatrixHandle = getUniformHandle("uMVPMatrix")
    private val fragmentColorHandle = getUniformHandle("uColor")

    @Suppress("MemberVisibilityCanBePrivate")
    var color: FloatArray = floatArrayOf(1F, 1F, 1F, 1F)

    override fun onPreDraw(drawable: GlDrawable, modelViewProjectionMatrix: FloatArray) {
        super.onPreDraw(drawable, modelViewProjectionMatrix)

        // Copy the modelViewProjectionMatrix over.
        glUniformMatrix4fv(vertexMvpMatrixHandle.value, 1, false,
                modelViewProjectionMatrix)
        Egloo.checkGlError("glUniformMatrix4fv")

        // Copy the color vector in.
        glUniform4fv(fragmentColorHandle.value, 1, color)
        Egloo.checkGlError("glUniform4fv")

        // Enable the "aPosition" vertex attribute.
        glEnableVertexAttribArray(vertexPositionHandle.uvalue)
        Egloo.checkGlError("glEnableVertexAttribArray")

        // Connect vertexBuffer to "aPosition".
        glVertexAttribPointer(vertexPositionHandle.uvalue, drawable.coordsPerVertex,
                GL_FLOAT, false, drawable.vertexStride, drawable.vertexArray)
        Egloo.checkGlError("glVertexAttribPointer")
    }

    override fun onPostDraw(drawable: GlDrawable) {
        super.onPostDraw(drawable)
        glDisableVertexAttribArray(vertexPositionHandle.uvalue)
    }

    companion object {
        private const val VERTEX_SHADER =
                "" +
                        "uniform mat4 uMVPMatrix;\n" +
                        "attribute vec4 aPosition;\n" +
                        "void main() {\n" +
                        "    gl_Position = uMVPMatrix * aPosition;\n" +
                        "}\n"

        private const val FRAGMENT_SHADER =
                "" +
                        "precision mediump float;\n" +
                        "uniform vec4 uColor;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = uColor;\n" +
                        "}\n"
    }
}