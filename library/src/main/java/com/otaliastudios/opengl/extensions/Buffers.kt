package com.otaliastudios.opengl.extensions

import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

fun FloatArray.toBuffer(): FloatBuffer {
    val buffer = ByteBuffer
            .allocateDirect(size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(this)
    buffer.flip()
    return buffer
}

fun floatBufferOf(vararg elements: Float): FloatBuffer {
    return floatArrayOf(*elements).toBuffer()
}

fun floatBufferOf(size: Int): FloatBuffer {
    return ByteBuffer
            .allocateDirect(size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().also {
                it.limit(it.capacity())
            }
}