package com.otaliastudios.opengl.program

import android.graphics.RectF
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.draw.Gl2dDrawable
import com.otaliastudios.opengl.draw.GlDrawable
import com.otaliastudios.opengl.extensions.floatBufferOf
import com.otaliastudios.opengl.texture.GlTexture
import java.lang.RuntimeException


/**
 * Base implementation for a [GlProgram] that draws textures.
 */
@Suppress("unused")
open class GlTextureProgram protected constructor(
        handle: Int,
        ownsHandle: Boolean,
        /* An attribute vec4 within the vertex shader that will contain the vertex position. */
        vertexPositionName: String,
        /* A uniform mat4 within the vertex shader that will contain the MVP matrix. */
        vertexMvpMatrixName: String,
        textureCoordsName: String?, // enforce not null?
        textureTransformName: String? // enforce not null?
): GlProgram(handle, ownsHandle) {

    @JvmOverloads
    constructor(
            vertexShader: String = SIMPLE_VERTEX_SHADER,
            fragmentShader: String = SIMPLE_FRAGMENT_SHADER,
            vertexPositionName: String = "aPosition",
            vertexMvpMatrixName: String = "uMVPMatrix",
            textureCoordsName: String? = "aTextureCoord",
            textureTransformName: String? = "uTexMatrix"
    ) : this(
            create(vertexShader, fragmentShader),
            true,
            vertexPositionName,
            vertexMvpMatrixName,
            textureCoordsName,
            textureTransformName
    )

    @JvmOverloads
    constructor(
            handle: Int,
            vertexPositionName: String = "aPosition",
            vertexMvpMatrixName: String = "uMVPMatrix",
            textureCoordsName: String? = "aTextureCoord",
            textureTransformName: String? = "uTexMatrix"
    ) : this(
            handle,
            false,
            vertexPositionName,
            vertexMvpMatrixName,
            textureCoordsName,
            textureTransformName
    )

    var textureTransform: FloatArray = Egloo.IDENTITY_MATRIX.clone()
    private val textureTransformHandle = textureTransformName?.let { getUniformHandle(it) }

    private var textureCoordsBuffer = floatBufferOf(8)
    private val textureCoordsHandle = textureCoordsName?.let { getAttribHandle(it) }

    private val vertexPositionHandle = getAttribHandle(vertexPositionName)
    private val vertexMvpMatrixHandle = getUniformHandle(vertexMvpMatrixName)

    private val lastDrawableBounds = RectF()
    private var lastDrawableVersion = -1
    private var lastDrawable: Gl2dDrawable? = null

    /**
     * If not null, [GlTextureProgram] will care about the texture lifecycle: binding,
     * unbinding and destroying.
     */
    var texture: GlTexture? = null

    override fun onPreDraw(drawable: GlDrawable, modelViewProjectionMatrix: FloatArray) {
        super.onPreDraw(drawable, modelViewProjectionMatrix)
        if (drawable !is Gl2dDrawable) {
            throw RuntimeException("GlTextureProgram only supports 2D drawables.")
        }

        texture?.bind()

        // Pass the MVP matrix.
        vertexMvpMatrixHandle.let {
            GLES20.glUniformMatrix4fv(it.value, 1, false, modelViewProjectionMatrix, 0)
            Egloo.checkGlError("glUniformMatrix4fv")
        }

        // Pass the texture transformation matrix.
        textureTransformHandle?.let {
            GLES20.glUniformMatrix4fv(it.value, 1, false, textureTransform, 0)
            Egloo.checkGlError("glUniformMatrix4fv")
        }

        // Pass the vertices position.
        vertexPositionHandle.let {
            GLES20.glEnableVertexAttribArray(it.value)
            Egloo.checkGlError("glEnableVertexAttribArray")
            GLES20.glVertexAttribPointer(it.value, 2,
                    GLES20.GL_FLOAT,
                    false,
                    drawable.vertexStride,
                    drawable.vertexArray)
            Egloo.checkGlError("glVertexAttribPointer")
        }

        // We must compute the texture coordinates given the drawable vertex array.
        // To do this, we ask the drawable for its boundaries, then apply the texture
        // onto this rect.
        textureCoordsHandle?.let {
            // Compute only if drawable changed. If the version has not changed, the
            // textureCoordsBuffer should be in a good state already - just need to rewind.
            if (drawable != lastDrawable || drawable.vertexArrayVersion != lastDrawableVersion) {
                lastDrawable = drawable
                lastDrawableVersion = drawable.vertexArrayVersion
                drawable.getBounds(lastDrawableBounds)
                val coordinates = drawable.vertexCount * 2
                if (textureCoordsBuffer.capacity() < coordinates) {
                    textureCoordsBuffer = floatBufferOf(coordinates)
                }
                textureCoordsBuffer.clear()
                textureCoordsBuffer.limit(coordinates)
                for (i in 0 until coordinates) {
                    val isX = i % 2 == 0
                    val drawableValue = drawable.vertexArray.get(i)
                    val drawableMinValue = if (isX) lastDrawableBounds.left else lastDrawableBounds.bottom
                    val drawableMaxValue = if (isX) lastDrawableBounds.right else lastDrawableBounds.top
                    val drawableFraction = (drawableValue - drawableMinValue) / (drawableMaxValue - drawableMinValue)
                    val textureValue = 0F + drawableFraction * 1F // tex value goes from 0 to 1
                    textureCoordsBuffer.put(i, textureValue)
                }
            } else {
                textureCoordsBuffer.rewind()
            }

            GLES20.glEnableVertexAttribArray(it.value)
            Egloo.checkGlError("glEnableVertexAttribArray")
            GLES20.glVertexAttribPointer(it.value, 2,
                    GLES20.GL_FLOAT,
                    false,
                    drawable.vertexStride,
                    textureCoordsBuffer)
            Egloo.checkGlError("glVertexAttribPointer")
        }
    }


    override fun onPostDraw(drawable: GlDrawable) {
        super.onPostDraw(drawable)
        vertexPositionHandle.let {
            GLES20.glDisableVertexAttribArray(it.value)
        }
        textureCoordsHandle?.let {
            GLES20.glDisableVertexAttribArray(it.value)
        }
        texture?.unbind()
        Egloo.checkGlError("onPostDraw end")
    }

    override fun release() {
        super.release()
        texture?.release()
        texture = null
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