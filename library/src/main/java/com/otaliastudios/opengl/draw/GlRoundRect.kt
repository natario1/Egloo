package com.otaliastudios.opengl.draw

import android.graphics.RectF
import android.opengl.GLES20
import androidx.annotation.Px
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.extensions.floatBufferOf
import java.nio.FloatBuffer
import kotlin.math.*

@Suppress("unused")
open class GlRoundRect : Gl2dDrawable() {

    companion object {
        private const val POINTS_PER_CORNER = 20
    }

    private var topLeftCorner = 0F
    private var topRightCorner = 0F
    private var bottomLeftCorner = 0F
    private var bottomRightCorner = 0F
    private var top = 1F
    private var bottom = -1F
    private var left = -1F
    private var right = 1F
    private var currentVertexCount = 1

    override var vertexArray = floatBufferOf((4 * POINTS_PER_CORNER + 2) * coordsPerVertex)

    override val vertexCount: Int
        get() = currentVertexCount

    init {
        recompute()
    }

    @Suppress("unused")
    fun setCornersPx(@Px corners: Float) {
        setCornersPx(corners, corners, corners, corners)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setCornersPx(@Px topLeft: Float, @Px topRight: Float, @Px bottomLeft: Float,
                     @Px bottomRight: Float) {
        topLeftCorner = topLeft
        topRightCorner = topRight
        bottomLeftCorner = bottomLeft
        bottomRightCorner = bottomRight
        recompute()
    }

    @Suppress("unused")
    fun setRect(rect: RectF) {
        setRect(rect.left, rect.top, rect.right, rect.bottom)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setRect(left: Float, top: Float, right: Float, bottom: Float) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        recompute()
    }

    override fun onViewportSizeChanged() {
        super.onViewportSizeChanged()
        recompute()
    }

    private fun recompute() {
        val array = vertexArray
        array.clear()

        // Insert the center point.
        val centerX = (right + left) / 2F
        val centerY = (top + bottom) / 2F
        array.put(centerX)
        array.put(centerY)

        val hasCorners = viewportHeight > 0 && viewportWidth > 0

        // Top left corner.
        val hasTopLeftCorner = hasCorners && topLeftCorner > 0F
        if (hasTopLeftCorner) {
            val cornerWidth = topLeftCorner / viewportWidth * 2F // -1...1 so 2
            val cornerHeight = topLeftCorner / viewportHeight * 2F
            val cornerPivotX = left + cornerWidth
            val cornerPivotY = top - cornerHeight
            addCornerArc(array, cornerPivotX, cornerPivotY,
                    cornerWidth, cornerHeight, 180)
        } else {
            array.put(left)
            array.put(top)
        }

        // Top right corner.
        val hasTopRightCorner = hasCorners && topRightCorner > 0F
        if (hasTopRightCorner) {
            val cornerWidth = topRightCorner / viewportWidth * 2F
            val cornerHeight = topRightCorner / viewportHeight * 2F
            val cornerPivotX = right - cornerWidth
            val cornerPivotY = top - cornerHeight
            addCornerArc(array, cornerPivotX, cornerPivotY,
                    cornerWidth, cornerHeight, 90)
        } else {
            array.put(right)
            array.put(top)
        }

        // Bottom right corner.
        val hasBottomRightCorner = hasCorners && bottomRightCorner > 0F
        if (hasBottomRightCorner) {
            val cornerWidth = bottomRightCorner / viewportWidth * 2F
            val cornerHeight = bottomRightCorner / viewportHeight * 2F
            val cornerPivotX = right - cornerWidth
            val cornerPivotY = bottom + cornerHeight
            addCornerArc(array, cornerPivotX, cornerPivotY,
                    cornerWidth, cornerHeight, 0)
        } else {
            array.put(right)
            array.put(bottom)
        }

        // Bottom left corner.
        val hasBottomLeftCorner = hasCorners && bottomLeftCorner > 0F
        if (hasBottomLeftCorner) {
            val cornerWidth = bottomLeftCorner / viewportWidth * 2F
            val cornerHeight = bottomLeftCorner / viewportHeight * 2F
            val cornerPivotX = left + cornerWidth
            val cornerPivotY = bottom + cornerHeight
            addCornerArc(array, cornerPivotX, cornerPivotY,
                    cornerWidth, cornerHeight, -90)
        } else {
            array.put(left)
            array.put(bottom)
        }

        // Close the fan
        array.put(array.get(2))
        array.put(array.get(3))
        currentVertexCount = array.position() / coordsPerVertex
        array.rewind()
    }

    private fun addCornerArc(array: FloatBuffer,
                             pivotX: Float, pivotY: Float,
                             width: Float, height: Float,
                             startAngle: Int) {
        val endAngle = startAngle - 90 // Move 90 clockwise
        val points = POINTS_PER_CORNER
        var t = 0F
        val tDelta = 1F / (points - 1)
        repeat(points) {
            val angle = startAngle + t * (endAngle - startAngle)
            val radians = (angle * PI / 180).toFloat()
            // ellipse equation in polar coordinates
            val radius = (width * height) / sqrt((
                    width * sin(radians)).pow(2) +
                    (height * cos(radians)).pow(2))
            array.put(pivotX + radius * cos(radians))
            array.put(pivotY + radius * sin(radians))
            t += tDelta
        }
    }

    override fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)
        Egloo.checkGlError("glDrawArrays")
    }
}