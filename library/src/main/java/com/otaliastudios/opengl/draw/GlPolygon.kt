package com.otaliastudios.opengl.draw

import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.extensions.floatBufferOf
import com.otaliastudios.opengl.extensions.scale
import com.otaliastudios.opengl.extensions.translate
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
open class GlPolygon(private val sides: Int): Gl2dDrawable() {
    init {
        if (sides < 3) {
            throw IllegalArgumentException("Polygon should have at least 3 sides.")
        }
    }

    private var viewportTranslationX = 0F
    private var viewportTranslationY = 0F
    private var viewportScaleX = 1F
    private var viewportScaleY = 1F

    /**
     * The polygon radius. The value 1 is half the smallest viewport dimension.
     * For example, a [GlCircle] with radius 1 will touch the viewport borders exactly.
     */
    var radius = 1F
        set(value) {
            field = value
            updateArray()
        }

    var rotation = 0F
        set(value) {
            field = value % 360
            updateArray()
        }

    var centerX = 0F
        set(value) {
            field = value
            updateArray()
            onViewportSizeChanged()
        }

    var centerY = 0F
        set(value) {
            field = value
            updateArray()
            onViewportSizeChanged()
        }

    override var vertexArray = floatBufferOf((sides + 2) * coordsPerVertex)

    init {
        updateArray()
    }

    private fun updateArray() {
        val array = vertexArray
        array.clear()
        array.put(centerX)
        array.put(centerY)
        var angle = rotation * (PI / 180F).toFloat()
        val step = (2F * PI).toFloat() / sides
        repeat(sides) {
            array.put(centerX + radius * cos(angle))
            array.put(centerY + radius * sin(angle))
            angle += step
        }
        array.put(array.get(2)) // Close the fan
        array.put(array.get(3)) // Close the fan
        array.flip()
    }

    override fun onViewportSizeChanged() {
        super.onViewportSizeChanged()
        // Undo the previous modifications.
        modelMatrix.scale(x = 1F / viewportScaleX, y = 1F / viewportScaleY)
        modelMatrix.translate(x = -viewportTranslationX, y = -viewportTranslationY)
        // Compute the new ones.
        if (viewportWidth > viewportHeight) {
            viewportScaleX = viewportHeight.toFloat() / viewportWidth
            viewportScaleY = 1F
            viewportTranslationX = centerX * (1 - viewportScaleX)
            viewportTranslationY = 0F
        } else if (viewportWidth < viewportHeight) {
            viewportScaleY = viewportWidth.toFloat() / viewportHeight
            viewportScaleX = 1F
            viewportTranslationY = centerY * (1 - viewportScaleY)
            viewportTranslationX = 0F
        } else {
            viewportScaleX = 1F
            viewportScaleY = 1F
            viewportTranslationX = 0F
            viewportTranslationY = 0F
        }
        // Apply the new ones.
        modelMatrix.translate(x = viewportTranslationX, y = viewportTranslationY)
        modelMatrix.scale(x = viewportScaleX, y = viewportScaleY)
    }

    override fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)
        Egloo.checkGlError("glDrawArrays")
    }
}