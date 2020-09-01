package com.otaliastudios.opengl.extensions

import com.otaliastudios.opengl.types.ByteBuffer
import com.otaliastudios.opengl.types.byteBuffer
import com.otaliastudios.opengl.types.FloatBuffer
import com.otaliastudios.opengl.types.floatBuffer
import com.otaliastudios.opengl.types.IntBuffer
import com.otaliastudios.opengl.types.intBuffer
import com.otaliastudios.opengl.types.ShortBuffer
import com.otaliastudios.opengl.types.shortBuffer

public fun FloatArray.toBuffer(): FloatBuffer = floatBuffer(size).also {
    it.put(this)
    it.flip()
}

public fun ShortArray.toBuffer(): ShortBuffer = shortBuffer(size).also {
    it.put(this)
    it.flip()
}

public fun IntArray.toBuffer(): IntBuffer = intBuffer(size).also {
    it.put(this)
    it.flip()
}

public fun ByteArray.toBuffer(): ByteBuffer = byteBuffer(size).also {
    it.put(this)
    it.flip()
}

public fun floatBufferOf(vararg elements: Float): FloatBuffer {
    return floatArrayOf(*elements).toBuffer()
}

public fun shortBufferOf(vararg elements: Short): ShortBuffer {
    return shortArrayOf(*elements).toBuffer()
}

public fun intBufferOf(vararg elements: Int): IntBuffer {
    return intArrayOf(*elements).toBuffer()
}

public fun byteBufferOf(vararg elements: Byte): ByteBuffer {
    return byteArrayOf(*elements).toBuffer()
}

@Deprecated("Do not use this.", replaceWith = ReplaceWith("FloatBuffer(size)"))
public fun floatBufferOf(size: Int): FloatBuffer = floatBuffer(size)

@Deprecated("Do not use this.",  replaceWith = ReplaceWith("ByteBuffer(size)"))
public fun byteBufferOf(size: Int): ByteBuffer = byteBuffer(size)
