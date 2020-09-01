package com.otaliastudios.opengl.scene

import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.draw.GlDrawable
import com.otaliastudios.opengl.program.GlProgram
import com.otaliastudios.opengl.core.GlViewportAware
import com.otaliastudios.opengl.internal.matrixClone
import com.otaliastudios.opengl.internal.matrixMultiply

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
public open class GlScene : GlViewportAware() {

    @Suppress("MemberVisibilityCanBePrivate")
    public val projectionMatrix: FloatArray = matrixClone(Egloo.IDENTITY_MATRIX)

    @Suppress("MemberVisibilityCanBePrivate")
    public val viewMatrix: FloatArray = matrixClone(Egloo.IDENTITY_MATRIX)

    private val modelViewMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private fun computeModelViewProjectionMatrix(drawable: GlDrawable) {
        matrixMultiply(modelViewMatrix, viewMatrix, drawable.modelMatrix)
        matrixMultiply(modelViewProjectionMatrix, projectionMatrix, modelViewMatrix)
    }

    public fun draw(program: GlProgram, drawable: GlDrawable) {
        ensureViewportSize()
        drawable.setViewportSize(viewportWidth, viewportHeight)

        computeModelViewProjectionMatrix(drawable)
        program.draw(drawable, modelViewProjectionMatrix)
    }
}
