package com.otaliastudios.opengl.draw

import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.extensions.floatBufferOf
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

    var radius = 1F
        set(value) {
            field = value
            vertexArray = generateArray()
        }

    var rotation = 0F
        set(value) {
            field = value % 360
            vertexArray = generateArray()
        }

    override var vertexArray: FloatBuffer = generateArray()

    private fun generateArray(): FloatBuffer {
        val array = floatBufferOf((sides + 2) * coordsPerVertex)
        array.put(0F) // Center X
        array.put(0F) // Center Y
        var angle = rotation * (PI / 180F).toFloat()
        val step = (2F * PI).toFloat() / sides
        repeat(sides) {
            array.put(radius * cos(angle))
            array.put(radius * sin(angle))
            angle += step
        }
        array.put(array.get(2)) // Close the fan
        array.put(array.get(3)) // Close the fan
        array.rewind()
        return array
    }

    override fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)
        Egloo.checkGlError("glDrawArrays")
    }
}