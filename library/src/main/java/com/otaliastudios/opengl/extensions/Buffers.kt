package com.otaliastudios.opengl.extensions

import com.otaliastudios.opengl.core.Egloo
import java.nio.*

fun IntArray.toBuffer(): IntBuffer {
    val buffer = ByteBuffer
            .allocateDirect(size * Egloo.SIZE_OF_INT)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
            .put(this)
    buffer.flip()
    return buffer
}

fun intBufferOf(vararg elements: Int): IntBuffer {
    return intArrayOf(*elements).toBuffer()
}

fun intBufferOf(size: Int): IntBuffer {
    return ByteBuffer
            .allocateDirect(size * Egloo.SIZE_OF_INT)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer().also {
                it.limit(it.capacity())
            }
}

fun ShortArray.toBuffer(): ShortBuffer {
    val buffer = ByteBuffer
            .allocateDirect(size * Egloo.SIZE_OF_SHORT)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(this)
    buffer.flip()
    return buffer
}

fun shortBufferOf(vararg elements: Short): ShortBuffer {
    return shortArrayOf(*elements).toBuffer()
}

fun shortBufferOf(size: Int): ShortBuffer {
    return ByteBuffer
            .allocateDirect(size * Egloo.SIZE_OF_SHORT)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer().also {
                it.limit(it.capacity())
            }
}

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