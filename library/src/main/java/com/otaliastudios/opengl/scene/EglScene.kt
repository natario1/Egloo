package com.otaliastudios.opengl.scene


import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Build
import androidx.annotation.RequiresApi
import com.otaliastudios.opengl.core.Egl
import com.otaliastudios.opengl.draw.EglDrawable
import com.otaliastudios.opengl.program.EglFlatProgram
import com.otaliastudios.opengl.program.EglProgram
import com.otaliastudios.opengl.program.EglTextureProgram

/**
 * Scenes are used to draw [EglDrawable]s through [EglProgram]s
 * and contain information about the [projectionMatrix] and the [viewMatrix],
 * both of which can be accessed and modified.
 */
open class EglScene {

    val projectionMatrix = Egl.IDENTITY_MATRIX.clone()
    val viewMatrix = Egl.IDENTITY_MATRIX.clone()

    private val modelViewMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private fun computeModelViewProjectionMatrix(drawable: EglDrawable) {
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix,0, drawable.modelMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)
    }

    fun drawFlat(drawable: EglDrawable, program: EglFlatProgram, color: FloatArray) {
        computeModelViewProjectionMatrix(drawable)
        program.draw(modelViewProjectionMatrix,
                color,
                drawable.vertexArray, 0, drawable.vertexCount,
                drawable.vertexStride, drawable.coordsPerVertex)
    }

    fun drawTexture(drawable: EglDrawable, program: EglTextureProgram, textureId: Int, textureMatrix: FloatArray) {
        computeModelViewProjectionMatrix(drawable)
        program.draw(modelViewProjectionMatrix,
                textureId, textureMatrix,
                drawable.vertexArray, 0, drawable.vertexCount,
                drawable.vertexStride, drawable.coordsPerVertex,
                drawable.texCoordArray, drawable.texCoordStride)
    }

    companion object {
        internal val TAG = EglScene::class.java.simpleName
    }
}
