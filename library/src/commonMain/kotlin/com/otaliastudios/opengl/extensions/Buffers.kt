package com.otaliastudios.opengl.extensions

import com.otaliastudios.opengl.types.ByteBuffer
import com.otaliastudios.opengl.types.byteBuffer
import com.otaliastudios.opengl.types.FloatBuffer
import com.otaliastudios.opengl.types.floatBuffer
import com.otaliastudios.opengl.types.IntBuffer
import com.otaliastudios.opengl.types.intBuffer
import com.otaliastudios.opengl.types.ShortBuffer
import com.otaliastudios.opengl.types.shortBuffer

fun FloatArray.toBuffer() = floatBuffer(size).also {
    it.put(this)
    it.flip()
}

fun ShortArray.toBuffer() = shortBuffer(size).also {
    it.put(this)
    it.flip()
}

fun IntArray.toBuffer() = intBuffer(size).also {
    it.put(this)
    it.flip()
}

fun ByteArray.toBuffer() = byteBuffer(size).also {
    it.put(this)
    it.flip()
}

fun floatBufferOf(vararg elements: Float): FloatBuffer {
    return floatArrayOf(*elements).toBuffer()
}

fun shortBufferOf(vararg elements: Short): ShortBuffer {
    return shortArrayOf(*elements).toBuffer()
}

fun intBufferOf(vararg elements: Int): IntBuffer {
    return intArrayOf(*elements).toBuffer()
}

fun byteBufferOf(vararg elements: Byte): ByteBuffer {
    return byteArrayOf(*elements).toBuffer()
}

@Deprecated("Do not use this.", replaceWith = ReplaceWith("FloatBuffer(size)"))
fun floatBufferOf(size: Int) = floatBuffer(size)

@Deprecated("Do not use this.",  replaceWith = ReplaceWith("ByteBuffer(size)"))
fun byteBufferOf(size: Int) = byteBuffer(size)
