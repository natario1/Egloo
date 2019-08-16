package com.otaliastudios.opengl.draw

import android.graphics.RectF
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.extensions.floatBufferOf
import java.lang.IllegalArgumentException
import java.nio.FloatBuffer

@Suppress("unused")
abstract class Gl2dDrawable: GlDrawable() {
    final override val coordsPerVertex = 2
}