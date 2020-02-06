package com.otaliastudios.opengl.program

import android.graphics.RectF
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.draw.Gl2dDrawable
import com.otaliastudios.opengl.draw.Gl3dDrawable
import com.otaliastudios.opengl.draw.GlDrawable
import com.otaliastudios.opengl.draw.GlTexturable
import com.otaliastudios.opengl.extensions.floatBufferOf
import com.otaliastudios.opengl.geometry.Rect3F
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

    private val lastDrawableBounds3 = Rect3F()
    private val lastDrawableBounds2 = RectF()
    private var lastDrawableVersion = -1
    private var lastDrawable: GlDrawable? = null

    /**
     * If not null, [GlTextureProgram] will care about the texture lifecycle: binding,
     * unbinding and destroying.
     */
    var texture: GlTexture? = null

    override fun onPreDraw(drawable: GlDrawable, modelViewProjectionMatrix: FloatArray) {
        super.onPreDraw(drawable, modelViewProjectionMatrix)
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
            GLES20.glVertexAttribPointer(it.value, drawable.coordsPerVertex,
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
            val buffer = if (drawable is GlTexturable) {
                drawable.textureCoordsArray
            } else if (drawable == lastDrawable && drawable.vertexArrayVersion == lastDrawableVersion) {
                textureCoordsBuffer
            } else {
                lastDrawable = drawable
                lastDrawableVersion = drawable.vertexArrayVersion
                // NOTE: For 3D drawables we also allocate 3D texture coords which assumes that
                // we'll use a 3D texture even though it's not natively supported by Egloo. This
                // might add small overhead.
                val coordinates = drawable.vertexCount * drawable.coordsPerVertex
                if (textureCoordsBuffer.capacity() < coordinates) {
                    textureCoordsBuffer = floatBufferOf(coordinates)
                }
                textureCoordsBuffer.clear()
                textureCoordsBuffer.limit(coordinates)
                when (drawable) {
                    is Gl2dDrawable -> drawable.getBounds(lastDrawableBounds2)
                    is Gl3dDrawable -> drawable.getBounds(lastDrawableBounds3)
                    else -> throw RuntimeException("GlTextureDrawable only supports Gl2dDrawable or Gl3dDrawable")
                }
                var min = 0F
                var max = 0F
                for (i in 0 until coordinates) {
                    val value = drawable.vertexArray.get(i)
                    val dimension = i % drawable.coordsPerVertex
                    when {
                        drawable.coordsPerVertex == 2 && dimension == 0 -> {
                            min = lastDrawableBounds2.left
                            max = lastDrawableBounds2.right
                        }
                        drawable.coordsPerVertex == 2 && dimension == 1 -> {
                            min = lastDrawableBounds2.bottom
                            max = lastDrawableBounds2.top
                        }
                        drawable.coordsPerVertex == 3 && dimension == 0 -> {
                            min = lastDrawableBounds3.left
                            max = lastDrawableBounds3.right
                        }
                        drawable.coordsPerVertex == 3 && dimension == 1 -> {
                            min = lastDrawableBounds3.bottom
                            max = lastDrawableBounds3.top
                        }
                        drawable.coordsPerVertex == 3 && dimension == 2 -> {
                            min = lastDrawableBounds3.near
                            max = lastDrawableBounds3.far
                        }
                    }
                    val texValue = computeTextureCoordinate(drawable = drawable,
                            vertex = i / drawable.coordsPerVertex,
                            dimension = dimension,
                            value = value,
                            min = min,
                            max = max)
                    textureCoordsBuffer.put(texValue)
                }
                textureCoordsBuffer
            }

            val coords = when (drawable) {
                is GlTexturable -> drawable.textureCoordsPerVertex
                else -> drawable.coordsPerVertex
            }

            val stride = when (drawable) {
                is GlTexturable -> drawable.textureCoordsStride
                else -> drawable.vertexStride
            }

            buffer.rewind()
            GLES20.glEnableVertexAttribArray(it.value)
            Egloo.checkGlError("glEnableVertexAttribArray")
            GLES20.glVertexAttribPointer(it.value, coords,
                    GLES20.GL_FLOAT,
                    false,
                    stride,
                    buffer)
            Egloo.checkGlError("glVertexAttribPointer")
        }
    }

    // Returns the texture value for a given drawable vertex coordinate,
    // considering that texture values go from 0 to 1.
    protected open fun computeTextureCoordinate(drawable: GlDrawable,
                                                vertex: Int,
                                                dimension: Int,
                                                value: Float,
                                                min: Float,
                                                max: Float): Float {
        val fraction = (value - min) / (max - min)
        return 0F + fraction * 1F // in tex coords
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

        // Simple vertex shader for 2D textures
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

        // Simple fragment shader for 2D textures
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