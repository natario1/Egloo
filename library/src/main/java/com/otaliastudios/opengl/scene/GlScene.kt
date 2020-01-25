package com.otaliastudios.opengl.scene


import android.opengl.Matrix
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.draw.GlDrawable
import com.otaliastudios.opengl.program.GlProgram
import com.otaliastudios.opengl.core.GlViewportAware

/**
 * Scenes can be to draw [GlDrawable]s through [GlProgram]s.
 *
 * The advantage is that they contain information about the [projectionMatrix] and the [viewMatrix],
 * both of which can be accessed and modified and held by this single object.
 *
 * The [GlScene] object will combine these two with the drawables [GlDrawable.modelMatrix]
 * and pass the resulting model-view-projection matrix to the program.
 */
@Suppress("unused")
open class GlScene : GlViewportAware() {

    @Suppress("MemberVisibilityCanBePrivate")
    val projectionMatrix = Egloo.IDENTITY_MATRIX.clone()

    @Suppress("MemberVisibilityCanBePrivate")
    val viewMatrix = Egloo.IDENTITY_MATRIX.clone()

    private val modelViewMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private fun computeModelViewProjectionMatrix(drawable: GlDrawable) {
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix,0, drawable.modelMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)
    }

    fun draw(program: GlProgram, drawable: GlDrawable) {
        ensureViewportSize()
        drawable.setViewportSize(viewportWidth, viewportHeight)

        computeModelViewProjectionMatrix(drawable)
        program.draw(drawable, modelViewProjectionMatrix)
    }

    companion object {
        @Suppress("unused")
        internal val TAG = GlScene::class.java.simpleName
    }
}
