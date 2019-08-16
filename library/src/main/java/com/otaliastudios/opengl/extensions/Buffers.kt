package com.otaliastudios.opengl.extensions

import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

fun FloatArray.makeIdentity(): FloatArray {
    if (size != 16) throw RuntimeException("Need a 16 values matrix.")
    Matrix.setIdentityM(this, 0)
    return this
}

fun FloatArray.toBuffer(): FloatBuffer {
    val buffer = ByteBuffer
            .allocateDirect(size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(this)
    buffer.rewind()
    return buffer
}

fun floatBufferOf(vararg elements: Float): FloatBuffer {
    return floatArrayOf(*elements).toBuffer()
}

fun floatBufferOf(size: Int): FloatBuffer {
    return ByteBuffer
            .allocateDirect(size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
}