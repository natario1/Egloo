package com.otaliastudios.opengl.scene


import android.opengl.Matrix
import com.otaliastudios.opengl.core.Egl
import com.otaliastudios.opengl.draw.EglDrawable
import com.otaliastudios.opengl.program.EglProgram

/**
 * Scenes can be to draw [EglDrawable]s through [EglProgram]s.
 *
 * The advantage is that they contain information about the [projectionMatrix] and the [viewMatrix],
 * both of which can be accessed and modified and held by this single object.
 *
 * The [EglScene] object will combine these two with the drawables [EglDrawable.modelMatrix]
 * and pass the resulting model-view-projection matrix to the program.
 */
@Suppress("unused")
open class EglScene {

    @Suppress("MemberVisibilityCanBePrivate")
    val projectionMatrix = Egl.IDENTITY_MATRIX.clone()

    @Suppress("MemberVisibilityCanBePrivate")
    val viewMatrix = Egl.IDENTITY_MATRIX.clone()

    private val modelViewMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private fun computeModelViewProjectionMatrix(drawable: EglDrawable) {
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix,0, drawable.modelMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)
    }

    fun draw(program: EglProgram, drawable: EglDrawable) {
        computeModelViewProjectionMatrix(drawable)
        program.draw(drawable, modelViewProjectionMatrix)
    }

    companion object {
        @Suppress("unused")
        internal val TAG = EglScene::class.java.simpleName
    }
}
