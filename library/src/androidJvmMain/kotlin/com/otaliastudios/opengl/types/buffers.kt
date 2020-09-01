@file:JvmName("BuffersJvmKt")
package com.otaliastudios.opengl.types

import com.otaliastudios.opengl.core.Egloo
import java.nio.ByteOrder

public actual typealias Buffer = java.nio.Buffer
public actual typealias FloatBuffer = java.nio.FloatBuffer
public actual typealias ByteBuffer = java.nio.ByteBuffer
public actual typealias ShortBuffer = java.nio.ShortBuffer
public actual typealias IntBuffer = java.nio.IntBuffer

public actual fun byteBuffer(size: Int): ByteBuffer = ByteBuffer
        .allocateDirect(size * Egloo.SIZE_OF_BYTE)
        .order(ByteOrder.nativeOrder())
        .also { it.limit(it.capacity()) }

public actual fun shortBuffer(size: Int): ShortBuffer = byteBuffer(size * Egloo.SIZE_OF_SHORT).asShortBuffer()
public actual fun floatBuffer(size: Int): FloatBuffer = byteBuffer(size * Egloo.SIZE_OF_FLOAT).asFloatBuffer()
public actual fun intBuffer(size: Int): IntBuffer = byteBuffer(size * Egloo.SIZE_OF_INT).asIntBuffer()
