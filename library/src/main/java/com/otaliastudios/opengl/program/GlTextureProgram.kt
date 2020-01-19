package com.otaliastudios.opengl.program

import android.graphics.RectF
import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.draw.Gl2dDrawable
import com.otaliastudios.opengl.draw.GlDrawable
import com.otaliastudios.opengl.extensions.floatBufferOf
import java.lang.RuntimeException


/**
 * An [GlBaseTextureProgram] that uses a simple vertex shader and a texture fragment shader.
 */
@Suppress("unused")
open class GlTextureProgram @JvmOverloads constructor(
        vertexShader: String = SIMPLE_VERTEX_SHADER,
        fragmentShader: String = SIMPLE_FRAGMENT_SHADER,
        textureUnit: Int = GLES20.GL_TEXTURE0,
        textureTarget: Int = GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
        vertexPositionName: String = "aPosition",
        vertexMvpMatrixName: String = "uMVPMatrix",
        textureCoordsName: String = "aTextureCoord",
        textureTransformName: String = "uTexMatrix"
): GlBaseTextureProgram(
        vertexShader,
        fragmentShader,
        textureUnit,
        textureTarget
) {

    private val vertexPositionHandle = getAttribHandle(vertexPositionName)
    private val vertexMvpMatrixHandle = getUniformHandle(vertexMvpMatrixName)
    private val textureCoordsHandle = getAttribHandle(textureCoordsName)
    private val textureTransformHandle = getUniformHandle(textureTransformName)

    private val drawableBounds = RectF()
    private var textureCoordsBuffer = floatBufferOf(8)

    @Suppress("MemberVisibilityCanBePrivate")
    var textureTransform: FloatArray = Egloo.IDENTITY_MATRIX.clone()

    private fun ensureTextureCoordsBuffer(size: Int) {
        if (textureCoordsBuffer.capacity() < size) {
            textureCoordsBuffer = floatBufferOf(size)
        }
        textureCoordsBuffer.clear()
        textureCoordsBuffer.limit(size)
    }

    override fun onPreDraw(drawable: GlDrawable, modelViewProjectionMatrix: FloatArray) {
        super.onPreDraw(drawable, modelViewProjectionMatrix)
        if (drawable !is Gl2dDrawable) {
            throw RuntimeException("GlTextureProgram only supports 2D drawables.")
        }

        // Copy the modelViewProjectionMatrix over.
        GLES20.glUniformMatrix4fv(vertexMvpMatrixHandle.value, 1, false,
                modelViewProjectionMatrix, 0)
        Egloo.checkGlError("glUniformMatrix4fv")

        // Copy the texture transformation matrix over.
        GLES20.glUniformMatrix4fv(textureTransformHandle.value, 1, false,
                textureTransform, 0)
        Egloo.checkGlError("glUniformMatrix4fv")

        // Enable the "aPosition" vertex attribute.
        // Connect vertexBuffer to "aPosition".
        val vertexStride = 2 * Egloo.SIZE_OF_FLOAT
        GLES20.glEnableVertexAttribArray(vertexPositionHandle.value)
        Egloo.checkGlError("glEnableVertexAttribArray")
        GLES20.glVertexAttribPointer(vertexPositionHandle.value, 2,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                drawable.vertexArray)
        Egloo.checkGlError("glVertexAttribPointer")

        // We must compute the texture coordinates given the drawable vertex array.
        // To do this, we ask the drawable for its boundaries, then apply the texture
        // onto this rect.
        // TODO cache so that we only do this if drawable bounds have changed.
        //  Need a Drawable mechanism
        drawable.getBounds(drawableBounds)
        val coordinates = drawable.vertexCount * 2
        ensureTextureCoordsBuffer(coordinates)
        for (i in 0 until coordinates) {
            val isX = i % 2 == 0
            val drawableValue = drawable.vertexArray.get(i)
            val drawableMinValue = if (isX) drawableBounds.left else drawableBounds.bottom
            val drawableMaxValue = if (isX) drawableBounds.right else drawableBounds.top
            val drawableFraction = (drawableValue - drawableMinValue) / (drawableMaxValue - drawableMinValue)
            val textureValue = 0F + drawableFraction * 1F // tex value goes from 0 to 1
            textureCoordsBuffer.put(i, textureValue)
        }

        GLES20.glEnableVertexAttribArray(textureCoordsHandle.value)
        Egloo.checkGlError("glEnableVertexAttribArray")
        GLES20.glVertexAttribPointer(textureCoordsHandle.value, 2,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                textureCoordsBuffer)
        Egloo.checkGlError("glVertexAttribPointer")
    }

    override fun onPostDraw(drawable: GlDrawable) {
        GLES20.glDisableVertexAttribArray(vertexPositionHandle.value)
        GLES20.glDisableVertexAttribArray(textureCoordsHandle.value)
        super.onPostDraw(drawable)
    }

    companion object {
        @Suppress("unused")
        internal val TAG = GlTextureProgram::class.java.simpleName

        const val SIMPLE_VERTEX_SHADER =
                "" +
                        "uniform mat4 uMVPMatrix;\n" +
                        "uniform mat4 uTexMatrix;\n" +
                        "attribute vec4 aPosition;\n" +
                        "attribute vec4 aTextureCoord;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "void main() {\n" +
                        "    gl_Position = uMVPMatrix * aPosition;\n" +
                        "    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                        "}\n"

        const val SIMPLE_FRAGMENT_SHADER =
                "" +
                        "#extension GL_OES_EGL_image_external : require\n" +
                        "precision mediump float;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "uniform samplerExternalOES sTexture;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                        "}\n"
    }
}