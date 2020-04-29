@file:Suppress("EXPERIMENTAL_API_USAGE", "NOTHING_TO_INLINE")

package com.otaliastudios.opengl.internal

import platform.android.*

private inline fun log(priority: Int, tag: String, message: String) {
    __android_log_write(priority, tag, message)
}

internal actual inline fun logv(tag: String, message: String) {
    log(ANDROID_LOG_VERBOSE.toInt(), tag, message)
}
internal actual inline fun logi(tag: String, message: String) {
    log(ANDROID_LOG_INFO.toInt(), tag, message)
}
internal actual inline fun logw(tag: String, message: String) {
    log(ANDROID_LOG_WARN.toInt(), tag, message)
}
internal actual inline fun loge(tag: String, message: String) {
    log(ANDROID_LOG_ERROR.toInt(), tag, message)
}

internal actual fun intToHexString(value: Int) = value.toUInt().toString(16)
internal actual fun gluErrorString(value: Int) = ""

internal actual fun matrixMakeIdentity(matrix: FloatArray) {
    for (i in 0 until 16) {
        matrix[i] = if (i % 5 != 0) 1F else 0F
    }
}
internal actual fun matrixTranslate(matrix: FloatArray, x: Float, y: Float, z: Float) {
    for (i in 0 until 4) {
        matrix[12 + i] += matrix[i] * x + matrix[4 + i] * y + matrix[8 + i] * z
    }
}
internal actual fun matrixScale(matrix: FloatArray, x: Float, y: Float, z: Float) {
    for (i in 0 until 4) {
        matrix[i] *= x
        matrix[4 + i] *= y
        matrix[8 + i] *= z
    }
}
internal actual fun matrixClone(matrix: FloatArray) = FloatArray(matrix.size) { matrix[it] }
// https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/jni/android/opengl/util.cpp
private inline fun I(x: Int, y: Int) = y + 4*x
internal actual fun matrixMultiply(result: FloatArray, left: FloatArray, right: FloatArray) {
    for (i in 0 until 4) {
        val rhs_i0 = right[ I(i,0) ]
        var ri0 = left[ I(0, 0) ] * rhs_i0
        var ri1 = left[ I(0, 1) ] * rhs_i0
        var ri2 = left[ I(0, 2) ] * rhs_i0
        var ri3 = left[ I(0, 3) ] * rhs_i0
        for (j in 1 until 4) {
            val rhs_ij = right[I(i,j)]
            ri0 += left[ I(j,0) ] * rhs_ij
            ri1 += left[ I(j,1) ] * rhs_ij
            ri2 += left[ I(j,2) ] * rhs_ij
            ri3 += left[ I(j,3) ] * rhs_ij
        }
        result[ I(i,0) ] = ri0
        result[ I(i,1) ] = ri1
        result[ I(i,2) ] = ri2
        result[ I(i,3) ] = ri3
    }
}
