@file:Suppress("NOTHING_TO_INLINE")

package com.otaliastudios.opengl.internal

import com.otaliastudios.opengl.types.Buffer
import com.otaliastudios.opengl.types.FloatBuffer
import kotlinx.cinterop.*
import platform.android.ANDROID_LOG_ERROR
import platform.android.__android_log_write
import platform.gles2.GLsizeiptr

internal expect inline fun Int.toGLsizeiptr(): GLsizeiptr

internal actual val GL_TRUE = platform.gles2.GL_TRUE
internal actual val GL_SHADER_STORAGE_BUFFER = 0x90D2u // gles31 is not included...
internal actual val GL_VIEWPORT = platform.gles2.GL_VIEWPORT
internal actual val GL_NO_ERROR = platform.gles2.GL_NO_ERROR
internal actual val GL_UNSIGNED_BYTE = platform.gles2.GL_UNSIGNED_BYTE.toUInt()
internal actual val GL_FLOAT = platform.gles2.GL_FLOAT.toUInt()
internal actual val GL_RGBA = platform.gles2.GL_RGBA.toUInt()
internal actual val GL_TRIANGLES = platform.gles2.GL_TRIANGLES.toUInt()
internal actual val GL_TRIANGLE_FAN = platform.gles2.GL_TRIANGLE_FAN.toUInt()
internal actual val GL_TRIANGLE_STRIP = platform.gles2.GL_TRIANGLE_STRIP.toUInt()
internal actual val GL_TEXTURE0 = platform.gles2.GL_TEXTURE0.toUInt()
internal actual val GL_TEXTURE_EXTERNAL_OES = platform.gles2.GL_TEXTURE_EXTERNAL_OES.toUInt()
internal actual val GL_TEXTURE_MIN_FILTER = platform.gles2.GL_TEXTURE_MIN_FILTER.toUInt()
internal actual val GL_TEXTURE_MAG_FILTER = platform.gles2.GL_TEXTURE_MAG_FILTER.toUInt()
internal actual val GL_TEXTURE_WRAP_S = platform.gles2.GL_TEXTURE_WRAP_S.toUInt()
internal actual val GL_TEXTURE_WRAP_T = platform.gles2.GL_TEXTURE_WRAP_T.toUInt()
internal actual val GL_CLAMP_TO_EDGE = platform.gles2.GL_CLAMP_TO_EDGE
internal actual val GL_NEAREST = platform.gles2.GL_NEAREST.toFloat()
internal actual val GL_LINEAR = platform.gles2.GL_LINEAR.toFloat()
internal actual val GL_FRAMEBUFFER = platform.gles2.GL_FRAMEBUFFER.toUInt()
internal actual val GL_FRAMEBUFFER_COMPLETE = platform.gles2.GL_FRAMEBUFFER_COMPLETE.toUInt()
internal actual val GL_COLOR_ATTACHMENT0 = platform.gles2.GL_COLOR_ATTACHMENT0.toUInt()
internal actual val GL_COMPILE_STATUS = platform.gles2.GL_COMPILE_STATUS.toUInt()
internal actual val GL_LINK_STATUS = platform.gles2.GL_LINK_STATUS.toUInt()
internal actual val GL_VERTEX_SHADER = platform.gles2.GL_VERTEX_SHADER.toUInt()
internal actual val GL_FRAGMENT_SHADER = platform.gles2.GL_FRAGMENT_SHADER.toUInt()

internal actual inline fun glGenTextures(count: Int, array: UIntArray) = memScoped {
    val pointer = allocArray<UIntVar>(array.size)
    platform.gles2.glGenTextures(count, pointer)
    array.indices.forEach { array[it] = pointer[it] }
}
internal actual inline fun glDeleteTextures(count: Int, array: UIntArray)
        = platform.gles2.glDeleteTextures(count, array.toCValues())
internal actual inline fun glActiveTexture(unit: UInt)
        = platform.gles2.glActiveTexture(unit)
internal actual inline fun glBindTexture(target: UInt, texture: UInt)
        = platform.gles2.glBindTexture(target, texture)
internal actual inline fun glTexParameteri(target: UInt, parameter: UInt, value: Int)
        = platform.gles2.glTexParameteri(target, parameter, value)
internal actual inline fun glTexParameterf(target: UInt, parameter: UInt, value: Float)
        = platform.gles2.glTexParameterf(target, parameter, value)
internal actual inline fun glTexImage2D(target: UInt, level: Int, internalFormat: Int, width: Int, height: Int, border: Int, format: UInt, type: UInt, pixels: Buffer?)
        = platform.gles2.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels?.pointer())

internal actual inline fun glGenFramebuffers(count: Int, array: UIntArray) = memScoped {
    val pointer = allocArray<UIntVar>(array.size)
    platform.gles2.glGenFramebuffers(count, pointer)
    array.indices.forEach { array[it] = pointer[it] }
}
internal actual inline fun glDeleteFramebuffers(count: Int, array: UIntArray)
        = platform.gles2.glDeleteFramebuffers(count, array.toCValues())
internal actual inline fun glBindFramebuffer(target: UInt, framebuffer: UInt)
        = platform.gles2.glBindFramebuffer(target, framebuffer)
internal actual inline fun glCheckFramebufferStatus(target: UInt)
        = platform.gles2.glCheckFramebufferStatus(target)
internal actual inline fun glFramebufferTexture2D(target: UInt, attachment: UInt, textureTarget: UInt, texture: UInt, level: Int)
        = platform.gles2.glFramebufferTexture2D(target, attachment, textureTarget, texture, level)

internal actual inline fun glGenBuffers(count: Int, array: UIntArray) = memScoped {
    val pointer = allocArray<UIntVar>(array.size)
    platform.gles2.glGenBuffers(count, pointer)
    array.indices.forEach { array[it] = pointer[it] }
}
internal actual inline fun glBindBuffer(target: UInt, id: UInt)
        = platform.gles2.glBindBuffer(target, id)
internal actual inline fun glDeleteBuffers(count: Int, array: UIntArray)
        = platform.gles2.glDeleteBuffers(count, array.toCValues())
internal actual inline fun glBufferData(target: UInt, size: Int, usage: UInt)
        = platform.gles2.glBufferData(target, size.toGLsizeiptr(), null, usage)
internal actual inline fun glBindBufferBase(target: UInt, index: UInt, id: UInt)
        = platform.gles3.glBindBufferBase(target, index, id)

internal actual inline fun glCreateShader(type: UInt)
        = platform.gles2.glCreateShader(type)
internal actual inline fun glShaderSource(shader: UInt, source: String) = memScoped {
    val strings = listOf(source).toCStringArray(this)
    platform.gles2.glShaderSource(shader, 1, strings, null)
}
internal actual inline fun glCompileShader(shader: UInt)
        = platform.gles2.glCompileShader(shader)
internal actual inline fun glDeleteShader(shader: UInt)
        = platform.gles2.glDeleteShader(shader)
internal actual inline fun glGetShaderInfoLog(shader: UInt): String {
    val lengths = IntArray(1)
    glGetShaderiv(shader, platform.gles2.GL_INFO_LOG_LENGTH.toUInt(), lengths)
    val length = lengths.first()
    return if (length <= 0) "" else memScoped {
        val chars = allocArray<ByteVar>(length)
        platform.gles2.glGetShaderInfoLog(shader, length, null, chars)
        chars.toKString() // breaks if not 0 terminated, but it should be!
    }
}
internal actual inline fun glGetShaderiv(shader: UInt, parameter: UInt, result: IntArray) = memScoped {
    val pointer = allocArray<IntVar>(result.size)
    platform.gles2.glGetShaderiv(shader, parameter, pointer)
    result.indices.forEach { result[it] = pointer[it] }
}

internal actual inline fun glCreateProgram()
        = platform.gles2.glCreateProgram()
internal actual inline fun glAttachShader(program: UInt, shader: UInt)
        = platform.gles2.glAttachShader(program, shader)
internal actual inline fun glLinkProgram(program: UInt)
        = platform.gles2.glLinkProgram(program)
internal actual inline fun glUseProgram(program: UInt)
        = platform.gles2.glUseProgram(program)
internal actual inline fun glDeleteProgram(program: UInt)
        = platform.gles2.glDeleteProgram(program)
internal actual inline fun glGetProgramInfoLog(program: UInt): String {
    val lengths = IntArray(1)
    glGetProgramiv(program, platform.gles2.GL_INFO_LOG_LENGTH.toUInt(), lengths)
    val length = lengths.first()
    return if (length <= 0) "" else memScoped {
        val chars = allocArray<ByteVar>(length)
        platform.gles2.glGetProgramInfoLog(program, length, null, chars)
        chars.toKString() // breaks if not 0 terminated, but it should be!
    }
}
internal actual inline fun glGetProgramiv(program: UInt, parameter: UInt, result: IntArray) = memScoped {
    val pointer = allocArray<IntVar>(result.size)
    platform.gles2.glGetProgramiv(program, parameter, pointer)
    result.indices.forEach { result[it] = pointer[it] }
}

internal actual inline fun glEnableVertexAttribArray(array: UInt)
        = platform.gles2.glEnableVertexAttribArray(array)
internal actual inline fun glDisableVertexAttribArray(array: UInt)
        = platform.gles2.glDisableVertexAttribArray(array)
internal actual inline fun glGetAttribLocation(program: UInt, name: String)
        = platform.gles2.glGetAttribLocation(program, name)
internal actual inline fun glGetUniformLocation(program: UInt, name: String)
        = platform.gles2.glGetUniformLocation(program, name)
internal actual inline fun glVertexAttribPointer(index: UInt, size: Int, type: UInt, normalized: Boolean, stride: Int, pointer: Buffer)
        = platform.gles2.glVertexAttribPointer(index, size, type, normalized.toByte().toUByte(), stride, pointer.pointer())
internal actual inline fun glUniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FloatBuffer)
        = platform.gles2.glUniformMatrix4fv(location, count, transpose.toByte().toUByte(), value.pointer().reinterpret())
internal actual inline fun glUniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FloatArray)
        // TODO not a fan of this toCValues() here, does not seem very efficient in a glUniformMatrix4fv call.
        = platform.gles2.glUniformMatrix4fv(location, count, transpose.toByte().toUByte(), value.toCValues())
internal actual inline fun glUniform4fv(location: Int, count: Int, value: FloatBuffer)
        = platform.gles2.glUniform4fv(location, count, value.pointer().reinterpret())
internal actual inline fun glUniform4fv(location: Int, count: Int, value: FloatArray)
        = platform.gles2.glUniform4fv(location, count, value.toCValues())

internal actual inline fun glGetIntegerv(parameter: UInt, array: IntArray) = memScoped {
    val pointer = allocArray<IntVar>(array.size)
    platform.gles2.glGetIntegerv(parameter, pointer)
    array.indices.forEach { array[it] = pointer[it] }
}
internal actual inline fun glGetError()
        = platform.gles2.glGetError()

internal actual inline fun glDrawArrays(mode: UInt, first: Int, count: Int)
        = platform.gles2.glDrawArrays(mode, first, count)
internal actual inline fun glDrawElements(mode: UInt, count: Int, type: UInt, indices: Buffer)
        = platform.gles2.glDrawElements(mode, count, type, indices.pointer())

