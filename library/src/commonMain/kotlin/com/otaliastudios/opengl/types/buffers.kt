package com.otaliastudios.opengl.types

expect abstract class Buffer {
    fun remaining(): Int
    fun hasRemaining(): Boolean
    fun capacity(): Int
    fun position(): Int
    fun position(position: Int): Buffer
    fun limit(): Int
    fun limit(limit: Int): Buffer
    fun clear(): Buffer
    fun rewind(): Buffer
    fun flip(): Buffer
}

// TODO should this be public and be applied to more structures?
internal interface Disposable {
    fun dispose()
}

fun Buffer.dispose() {
    if (this is Disposable) this.dispose()
}

expect abstract class FloatBuffer : Buffer {
    abstract fun get(): Float
    abstract fun get(index: Int): Float
    abstract fun put(value: Float): FloatBuffer
    fun put(values: FloatArray): FloatBuffer
}

expect abstract class ByteBuffer : Buffer {
    abstract fun get(): Byte
    abstract fun get(index: Int): Byte
    abstract fun put(value: Byte): ByteBuffer
    fun put(values: ByteArray): ByteBuffer
}

expect abstract class ShortBuffer : Buffer {
    abstract fun get(): Short
    abstract fun get(index: Int): Short
    abstract fun put(value: Short): ShortBuffer
    fun put(values: ShortArray): ShortBuffer
}

expect abstract class IntBuffer : Buffer {
    abstract fun get(): Int
    abstract fun get(index: Int): Int
    abstract fun put(value: Int): IntBuffer
    fun put(values: IntArray): IntBuffer
}

// Would have liked this to be upper case, but compiler complains
expect fun floatBuffer(size: Int): FloatBuffer
expect fun byteBuffer(size: Int): ByteBuffer
expect fun shortBuffer(size: Int): ShortBuffer
expect fun intBuffer(size: Int): IntBuffer