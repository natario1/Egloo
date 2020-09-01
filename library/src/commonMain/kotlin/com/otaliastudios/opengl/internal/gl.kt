package com.otaliastudios.opengl.internal

import com.otaliastudios.opengl.types.Buffer
import com.otaliastudios.opengl.types.FloatBuffer

internal expect val GL_TRUE: Int
internal expect val GL_SHADER_STORAGE_BUFFER: UInt
internal expect val GL_VIEWPORT: Int
internal expect val GL_NO_ERROR: Int
internal expect val GL_UNSIGNED_BYTE: UInt
internal expect val GL_FLOAT: UInt
internal expect val GL_RGBA: UInt
internal expect val GL_TRIANGLES: UInt
internal expect val GL_TRIANGLE_FAN: UInt
internal expect val GL_TRIANGLE_STRIP: UInt
internal expect val GL_TEXTURE0: UInt
internal expect val GL_TEXTURE_EXTERNAL_OES: UInt
internal expect val GL_TEXTURE_MIN_FILTER: UInt
internal expect val GL_TEXTURE_MAG_FILTER: UInt
internal expect val GL_TEXTURE_WRAP_S: UInt
internal expect val GL_TEXTURE_WRAP_T: UInt
internal expect val GL_CLAMP_TO_EDGE: Int
internal expect val GL_NEAREST: Float
internal expect val GL_LINEAR: Float
internal expect val GL_FRAMEBUFFER: UInt
internal expect val GL_FRAMEBUFFER_COMPLETE: UInt
internal expect val GL_COLOR_ATTACHMENT0: UInt
internal expect val GL_COMPILE_STATUS: UInt
internal expect val GL_LINK_STATUS: UInt
internal expect val GL_VERTEX_SHADER: UInt
internal expect val GL_FRAGMENT_SHADER: UInt

internal expect inline fun glGenTextures(count: Int, array: UIntArray)
internal expect inline fun glDeleteTextures(count: Int, array: UIntArray)
internal expect inline fun glActiveTexture(unit: UInt)
internal expect inline fun glBindTexture(target: UInt, texture: UInt)
internal expect inline fun glTexParameteri(target: UInt, parameter: UInt, value: Int)
internal expect inline fun glTexParameterf(target: UInt, parameter: UInt, value: Float)
internal expect inline fun glTexImage2D(target: UInt, level: Int, internalFormat: Int, width: Int, height: Int, border: Int, format: UInt, type: UInt, pixels: Buffer?)

internal expect inline fun glGenFramebuffers(count: Int, array: UIntArray)
internal expect inline fun glDeleteFramebuffers(count: Int, array: UIntArray)
internal expect inline fun glBindFramebuffer(target: UInt, framebuffer: UInt)
internal expect inline fun glCheckFramebufferStatus(target: UInt): UInt
internal expect inline fun glFramebufferTexture2D(target: UInt, attachment: UInt, textureTarget: UInt, texture: UInt, level: Int)

internal expect inline fun glGenBuffers(count: Int, array: UIntArray)
internal expect inline fun glBindBuffer(target: UInt, id: UInt)
internal expect inline fun glDeleteBuffers(count: Int, array: UIntArray)
internal expect inline fun glBufferData(target: UInt, size: Int, usage: UInt)
internal expect inline fun glBindBufferBase(target: UInt, index: UInt, id: UInt)

internal expect inline fun glCreateShader(type: UInt): UInt
internal expect inline fun glShaderSource(shader: UInt, source: String)
internal expect inline fun glCompileShader(shader: UInt)
internal expect inline fun glDeleteShader(shader: UInt)
internal expect inline fun glGetShaderInfoLog(shader: UInt): String
internal expect inline fun glGetShaderiv(shader: UInt, parameter: UInt, result: IntArray)

internal expect inline fun glCreateProgram(): UInt
internal expect inline fun glAttachShader(program: UInt, shader: UInt)
internal expect inline fun glLinkProgram(program: UInt)
internal expect inline fun glUseProgram(program: UInt)
internal expect inline fun glDeleteProgram(program: UInt)
internal expect inline fun glGetProgramInfoLog(program: UInt): String
internal expect inline fun glGetProgramiv(program: UInt, parameter: UInt, result: IntArray)

internal expect inline fun glEnableVertexAttribArray(array: UInt)
internal expect inline fun glDisableVertexAttribArray(array: UInt)
internal expect inline fun glGetAttribLocation(program: UInt, name: String): Int
internal expect inline fun glGetUniformLocation(program: UInt, name: String): Int
internal expect inline fun glVertexAttribPointer(index: UInt, size: Int, type: UInt, normalized: Boolean, stride: Int, pointer: Buffer)
internal expect inline fun glUniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FloatBuffer)
internal expect inline fun glUniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FloatArray)
internal expect inline fun glUniform4fv(location: Int, count: Int, value: FloatBuffer)
internal expect inline fun glUniform4fv(location: Int, count: Int, value: FloatArray)

internal expect inline fun glGetIntegerv(parameter: UInt, array: IntArray)
internal expect inline fun glGetError(): UInt

internal expect inline fun glDrawArrays(mode: UInt, first: Int, count: Int)
internal expect inline fun glDrawElements(mode: UInt, count: Int, type: UInt, indices: Buffer)