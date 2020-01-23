package com.otaliastudios.opengl.extensions

import android.opengl.Matrix
import com.otaliastudios.opengl.core.Egloo
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

fun FloatArray.toBuffer(): FloatBuffer {
    val buffer = ByteBuffer
            .allocateDirect(size * Egloo.SIZE_OF_FLOAT)
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
            .allocateDirect(size * Egloo.SIZE_OF_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().also {
                it.limit(it.capacity())
            }
}

fun ByteArray.toBuffer(): ByteBuffer {
    val buffer = ByteBuffer
            .allocateDirect(size)
            .order(ByteOrder.nativeOrder())
            .put(this)
    buffer.flip()
    return buffer
}

fun byteBufferOf(vararg elements: Byte): ByteBuffer {
    return byteArrayOf(*elements).toBuffer()
}

fun byteBufferOf(size: Int): ByteBuffer {
    return ByteBuffer
            .allocateDirect(size)
            .order(ByteOrder.nativeOrder())
            .also {
                it.limit(it.capacity())
            }
}