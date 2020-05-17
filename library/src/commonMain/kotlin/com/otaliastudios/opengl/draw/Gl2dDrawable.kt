package com.otaliastudios.opengl.draw

import com.otaliastudios.opengl.geometry.RectF
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
abstract class Gl2dDrawable: GlDrawable() {
    final override val coordsPerVertex = 2

    fun getBounds(rect: RectF) {
        var top = -Float.MAX_VALUE // not MIN_VALUE!
        var bottom = Float.MAX_VALUE
        var left = Float.MAX_VALUE
        var right = -Float.MAX_VALUE
        var count = 0
        while (vertexArray.hasRemaining()) {
            val value = vertexArray.get()
            if (count % 2 == 0) { // x coordinate
                left = min(left, value)
                right = max(right, value)
            } else { // y coordinate
                top = max(top, value)
                bottom = min(bottom, value)
            }
            count++
        }
        vertexArray.rewind()
        rect.set(left, top, right, bottom)
    }
}