package com.otaliastudios.opengl.types

public expect abstract class Buffer {
    public fun remaining(): Int
    public fun hasRemaining(): Boolean
    public fun capacity(): Int
    public fun position(): Int
    public fun position(position: Int): Buffer
    public fun limit(): Int
    public fun limit(limit: Int): Buffer
    public fun clear(): Buffer
    public fun rewind(): Buffer
    public fun flip(): Buffer
}

// TODO should this be public and be applied to more structures?
internal interface Disposable {
    fun dispose()
}

public fun Buffer.dispose() {
    if (this is Disposable) this.dispose()
}

public expect abstract class FloatBuffer : Buffer {
    public abstract fun get(): Float
    public abstract fun get(index: Int): Float
    public abstract fun put(value: Float): FloatBuffer
    public fun put(values: FloatArray): FloatBuffer
}

public expect abstract class ByteBuffer : Buffer {
    public abstract fun get(): Byte
    public abstract fun get(index: Int): Byte
    public abstract fun put(value: Byte): ByteBuffer
    public fun put(values: ByteArray): ByteBuffer
}

public expect abstract class ShortBuffer : Buffer {
    public abstract fun get(): Short
    public abstract fun get(index: Int): Short
    public abstract fun put(value: Short): ShortBuffer
    public fun put(values: ShortArray): ShortBuffer
}

public expect abstract class IntBuffer : Buffer {
    public abstract fun get(): Int
    public abstract fun get(index: Int): Int
    public abstract fun put(value: Int): IntBuffer
    public fun put(values: IntArray): IntBuffer
}

// Would have liked this to be upper case, but compiler complains
public expect fun floatBuffer(size: Int): FloatBuffer
public expect fun byteBuffer(size: Int): ByteBuffer
public expect fun shortBuffer(size: Int): ShortBuffer
public expect fun intBuffer(size: Int): IntBuffer