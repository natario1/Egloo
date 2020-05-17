@file:JvmName("BuffersJvmKt")
package com.otaliastudios.opengl.types

import com.otaliastudios.opengl.core.Egloo
import java.nio.ByteOrder

actual typealias Buffer = java.nio.Buffer
actual typealias FloatBuffer = java.nio.FloatBuffer
actual typealias ByteBuffer = java.nio.ByteBuffer
actual typealias ShortBuffer = java.nio.ShortBuffer
actual typealias IntBuffer = java.nio.IntBuffer

actual fun byteBuffer(size: Int) = ByteBuffer
        .allocateDirect(size * Egloo.SIZE_OF_BYTE)
        .order(ByteOrder.nativeOrder())
        .also { it.limit(it.capacity()) }

actual fun shortBuffer(size: Int) = byteBuffer(size * Egloo.SIZE_OF_SHORT).asShortBuffer()
actual fun floatBuffer(size: Int) = byteBuffer(size * Egloo.SIZE_OF_FLOAT).asFloatBuffer()
actual fun intBuffer(size: Int) = byteBuffer(size * Egloo.SIZE_OF_INT).asIntBuffer()
