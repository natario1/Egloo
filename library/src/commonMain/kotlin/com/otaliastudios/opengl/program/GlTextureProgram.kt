package com.otaliastudios.opengl.program

import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.draw.Gl2dDrawable
import com.otaliastudios.opengl.draw.GlDrawable
import com.otaliastudios.opengl.geometry.RectF
import com.otaliastudios.opengl.internal.*
import com.otaliastudios.opengl.internal.GL_FLOAT
import com.otaliastudios.opengl.internal.glDisableVertexAttribArray
import com.otaliastudios.opengl.internal.glEnableVertexAttribArray
import com.otaliastudios.opengl.internal.matrixClone
import com.otaliastudios.opengl.texture.GlTexture
import com.otaliastudios.opengl.types.floatBuffer
import com.otaliastudios.opengl.types.dispose
import kotlin.jvm.JvmOverloads


/**
 * Base implementation for a [GlProgram] that draws textures.
 */
@Suppress("unused")
public open class GlTextureProgram protected constructor(
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
    public constructor(
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
    public constructor(
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

    public var textureTransform: FloatArray = matrixClone(Egloo.IDENTITY_MATRIX)
    private val textureTransformHandle = textureTransformName?.let { getUniformHandle(it) }

    private var textureCoordsBuffer = floatBuffer(8)
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
    public var texture: GlTexture? = null

    override fun onPreDraw(drawable: GlDrawable, modelViewProjectionMatrix: FloatArray) {
        super.onPreDraw(drawable, modelViewProjectionMatrix)
        if (drawable !is Gl2dDrawable) {
            throw RuntimeException("GlTextureProgram only supports 2D drawables.")
        }

        texture?.bind()

        // Pass the MVP matrix.
        vertexMvpMatrixHandle.let {
            glUniformMatrix4fv(it.value, 1, false, modelViewProjectionMatrix)
            Egloo.checkGlError("glUniformMatrix4fv")
        }

        // Pass the texture transformation matrix.
        textureTransformHandle?.let {
            glUniformMatrix4fv(it.value, 1, false, textureTransform)
            Egloo.checkGlError("glUniformMatrix4fv")
        }

        // Pass the vertices position.
        vertexPositionHandle.let {
            glEnableVertexAttribArray(it.uvalue)
            Egloo.checkGlError("glEnableVertexAttribArray")
            glVertexAttribPointer(it.uvalue, 2,
                    GL_FLOAT,
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
                    textureCoordsBuffer.dispose()
                    textureCoordsBuffer = floatBuffer(coordinates)
                }
                textureCoordsBuffer.clear()
                textureCoordsBuffer.limit(coordinates)
                for (i in 0 until coordinates) {
                    val isX = i % 2 == 0
                    val value = drawable.vertexArray.get(i)
                    val min = if (isX) lastDrawableBounds.left else lastDrawableBounds.bottom
                    val max = if (isX) lastDrawableBounds.right else lastDrawableBounds.top
                    val texValue = computeTextureCoordinate(i / 2, drawable, value, min, max, isX)
                    textureCoordsBuffer.put(texValue)
                }
            }
            textureCoordsBuffer.rewind()

            glEnableVertexAttribArray(it.uvalue)
            Egloo.checkGlError("glEnableVertexAttribArray")
            glVertexAttribPointer(it.uvalue, 2,
                    GL_FLOAT,
                    false,
                    drawable.vertexStride,
                    textureCoordsBuffer)
            Egloo.checkGlError("glVertexAttribPointer")
        }
    }

    // Returns the texture value for a given drawable vertex coordinate,
    // considering that texture values go from 0 to 1.
    protected open fun computeTextureCoordinate(vertex: Int,
                                                drawable: Gl2dDrawable,
                                                value: Float,
                                                min: Float,
                                                max: Float,
                                                horizontal: Boolean): Float {
        val fraction = (value - min) / (max - min)
        return 0F + fraction * 1F // in tex coords
    }

    override fun onPostDraw(drawable: GlDrawable) {
        super.onPostDraw(drawable)
        vertexPositionHandle.let {
            glDisableVertexAttribArray(it.uvalue)
        }
        textureCoordsHandle?.let {
            glDisableVertexAttribArray(it.uvalue)
        }
        texture?.unbind()
        Egloo.checkGlError("onPostDraw end")
    }

    override fun release() {
        super.release()
        textureCoordsBuffer.dispose()
        texture?.release()
        texture = null
    }

    public companion object {

        public const val SIMPLE_VERTEX_SHADER: String =
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

        public const val SIMPLE_FRAGMENT_SHADER: String =
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