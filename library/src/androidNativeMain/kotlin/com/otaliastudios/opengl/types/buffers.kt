package com.otaliastudios.opengl.types

import com.otaliastudios.opengl.core.Egloo
import kotlinx.cinterop.*

public actual abstract class Buffer {

    protected abstract val capacity: Int
    protected var position: Int = 0
    protected abstract var limit: Int

    public actual fun remaining(): Int = limit - position
    public actual fun hasRemaining(): Boolean = remaining() > 0
    public actual fun capacity(): Int = capacity
    public actual fun position(): Int = position
    public actual fun limit(): Int = limit

    public actual fun position(position: Int): Buffer {
        this.position = position
        return this
    }

    public actual fun limit(limit: Int): Buffer {
        this.limit = limit
        return this
    }

    public actual fun clear(): Buffer {
        position = 0
        limit = capacity
        return this
    }

    public actual fun rewind(): Buffer {
        position = 0
        return this
    }

    public actual fun flip(): Buffer {
        limit = position
        position = 0
        return this
    }

    public abstract fun pointer(): CPointer<*>
}

public actual abstract class FloatBuffer : Buffer() {
    public actual abstract fun get(): Float
    public actual abstract fun get(index: Int): Float
    public actual abstract fun put(value: Float): FloatBuffer
    public actual fun put(values: FloatArray): FloatBuffer {
        values.forEach { put(it) }
        return this
    }
}

public actual abstract class ByteBuffer : Buffer() {
    public actual abstract fun get(): Byte
    public actual abstract fun get(index: Int): Byte
    public actual abstract fun put(value: Byte): ByteBuffer
    public actual fun put(values: ByteArray): ByteBuffer {
        values.forEach { put(it) }
        return this
    }
}

public actual abstract class ShortBuffer : Buffer() {
    public actual abstract fun get(): Short
    public actual abstract fun get(index: Int): Short
    public actual abstract fun put(value: Short): ShortBuffer
    public actual fun put(values: ShortArray): ShortBuffer {
        values.forEach { put(it) }
        return this
    }
}

public actual abstract class IntBuffer : Buffer() {
    public actual abstract fun get(): Int
    public actual abstract fun get(index: Int): Int
    public actual abstract fun put(value: Int): IntBuffer
    public actual fun put(values: IntArray): IntBuffer {
        values.forEach { put(it) }
        return this
    }
}

private class BufferImpl(capacity: Int, size: Int) : Disposable {
    private val base = nativeHeap.allocArray<ByteVar>(capacity * size)
    private val baseInt = base.reinterpret<IntVar>()
    private val baseShort = base.reinterpret<ShortVar>()
    private val baseFloat = base.reinterpret<FloatVar>()
    fun getByte(position: Int) = base[position]
    fun getInt(position: Int) = baseInt[position]
    fun getShort(position: Int) = baseShort[position]
    fun getFloat(position: Int) = baseFloat[position]
    fun putByte(position: Int, value: Byte) { base[position] = value }
    fun putInt(position: Int, value: Int) { baseInt[position] = value }
    fun putShort(position: Int, value: Short) { baseShort[position] = value }
    fun putFloat(position: Int, value: Float) { baseFloat[position] = value }
    fun getBytePointer(position: Int) = base + position
    fun getIntPointer(position: Int) = baseInt + position
    fun getShortPointer(position: Int) = baseShort + position
    fun getFloatPointer(position: Int) = baseFloat + position
    override fun dispose() {
        nativeHeap.free(base)
    }
}

public actual fun floatBuffer(size: Int): FloatBuffer = object: FloatBuffer() {
    override val capacity = size
    override var limit = size
    private val impl = BufferImpl(size, Egloo.SIZE_OF_FLOAT)
    override fun pointer() = impl.getFloatPointer(position) as CPointer<*>
    override fun get() = impl.getFloat(position++)
    override fun get(index: Int) = impl.getFloat(index)
    override fun put(value: Float): FloatBuffer {
        impl.putFloat(position++, value)
        return this
    }
}

public actual fun shortBuffer(size: Int): ShortBuffer = object: ShortBuffer() {
    override val capacity = size
    override var limit = size
    private val impl = BufferImpl(size, Egloo.SIZE_OF_SHORT)
    override fun pointer() = impl.getShortPointer(position) as CPointer<*>
    override fun get() = impl.getShort(position++)
    override fun get(index: Int) = impl.getShort(index)
    override fun put(value: Short): ShortBuffer {
        impl.putShort(position++, value)
        return this
    }
}

public actual fun intBuffer(size: Int): IntBuffer = object: IntBuffer() {
    override val capacity = size
    override var limit = size
    private val impl = BufferImpl(size, Egloo.SIZE_OF_INT)
    override fun pointer() = impl.getIntPointer(position) as CPointer<*>
    override fun get() = impl.getInt(position++)
    override fun get(index: Int) = impl.getInt(index)
    override fun put(value: Int): IntBuffer {
        impl.putInt(position++, value)
        return this
    }
}

public actual fun byteBuffer(size: Int): ByteBuffer = object: ByteBuffer() {
    override val capacity = size
    override var limit = size
    private val impl = BufferImpl(size, Egloo.SIZE_OF_BYTE)
    override fun pointer() = impl.getBytePointer(position) as CPointer<*>
    override fun get() = impl.getByte(position++)
    override fun get(index: Int) = impl.getByte(index)
    override fun put(value: Byte): ByteBuffer {
        impl.putByte(position++, value)
        return this
    }
}