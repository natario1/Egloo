package com.otaliastudios.opengl.draw

import android.graphics.RectF
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.extensions.floatBufferOf
import com.otaliastudios.opengl.geometry.Rect3F
import java.lang.IllegalArgumentException
import java.nio.FloatBuffer
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
abstract class Gl3dDrawable: GlDrawable() {
    final override val coordsPerVertex = 3

    fun getBounds(rect: Rect3F) {
        var top = -Float.MAX_VALUE // not MIN_VALUE!
        var bottom = Float.MAX_VALUE
        var left = Float.MAX_VALUE
        var right = -Float.MAX_VALUE
        var near = Float.MAX_VALUE
        var far = -Float.MAX_VALUE
        var count = 0
        while (vertexArray.hasRemaining()) {
            val value = vertexArray.get()
            if (count % 3 == 0) { // x coordinate
                left = min(left, value)
                right = max(right, value)
            } else if (count % 3 == 1) { // y coordinate
                top = max(top, value)
                bottom = min(bottom, value)
            } else {
                near = min(near, value)
                far = max(far, value)
            }
            count++
        }
        vertexArray.rewind()
        rect.set(left = left, top = top, near = near, right = right, bottom = bottom, far = far)
    }
}