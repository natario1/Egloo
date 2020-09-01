package com.otaliastudios.opengl.internal

internal expect inline fun logv(tag: String, message: String)
internal expect inline fun logi(tag: String, message: String)
internal expect inline fun logw(tag: String, message: String)
internal expect inline fun loge(tag: String, message: String)

internal expect fun intToHexString(value: Int): String
internal expect fun gluErrorString(value: Int): String

internal expect fun matrixMakeIdentity(matrix: FloatArray)
internal expect fun matrixTranslate(matrix: FloatArray, x: Float, y: Float, z: Float)
internal expect fun matrixScale(matrix: FloatArray, x: Float, y: Float, z: Float)
internal expect fun matrixRotate(matrix: FloatArray, angle: Float, x: Float, y: Float, z: Float)
internal expect fun matrixClone(matrix: FloatArray): FloatArray
internal expect fun matrixMultiply(result: FloatArray, left: FloatArray, right: FloatArray)
