@file:Suppress("NOTHING_TO_INLINE")

package com.otaliastudios.opengl.internal

import platform.android.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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
        matrix[i] = if (i % 5 == 0) 1F else 0F
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
private val tempMatrix1 = FloatArray(16)
private val tempMatrix2 = FloatArray(16)
private fun matrixSetRotate(matrix: FloatArray, angle: Float, x: Float, y: Float, z: Float) {
    matrix[3] = 0F
    matrix[7] = 0F
    matrix[11] = 0F
    matrix[12] = 0F
    matrix[13] = 0F
    matrix[14] = 0F
    matrix[15] = 1F
    val a = (angle * PI / 180F).toFloat()
    val s = sin(a)
    val c = cos(a)
    if (1.0f == x && 0.0f == y && 0.0f == z) {
        matrix[5] = c
        matrix[10] = c
        matrix[6] = s
        matrix[9] = -s
        matrix[1] = 0F
        matrix[2] = 0F
        matrix[4] = 0F
        matrix[8] = 0F
        matrix[0] = 1F
    } else if (0.0f == x && 1.0f == y && 0.0f == z) {
        matrix[0] = c
        matrix[10] = c
        matrix[8] = s
        matrix[2] = -s
        matrix[1] = 0F
        matrix[4] = 0F
        matrix[6] = 0F
        matrix[9] = 0F
        matrix[5] = 1F
    } else if (0.0f == x && 0.0f == y && 1.0f == z) {
        matrix[0] = c
        matrix[5] = c
        matrix[1] = s
        matrix[4] = -s
        matrix[2] = 0F
        matrix[6] = 0F
        matrix[8] = 0F
        matrix[9] = 0F
        matrix[10] = 1F
    } else {
        val normx: Float
        val normy: Float
        val normz: Float
        val length = sqrt((x * x) + (y * y) + (z * z))
        if (length == 1F) {
            normx = x
            normy = y
            normz = z
        } else {
            normx = x / length
            normy = y / length
            normz = z / length
        }
        val nc = 1.0F - c
        val xy = normx * normy
        val yz = normy * normz
        val zx = normz * normx
        val xs = normx * s
        val ys = normy * s
        val zs = normz * s
        matrix[0] = normx * normx * nc + c
        matrix[4] = xy * nc - zs
        matrix[8] = zx * nc + ys
        matrix[1] = xy * nc + zs
        matrix[5] = normy * normy * nc + c
        matrix[9] = yz * nc - xs
        matrix[2] = zx * nc - ys
        matrix[6] = yz * nc + xs
        matrix[10] = normz * normz * nc + c
    }
}
internal actual fun matrixRotate(matrix: FloatArray, angle: Float, x: Float, y: Float, z: Float) {
    matrixSetRotate(tempMatrix1, angle, x, y, z)
    matrixMultiply(tempMatrix2, matrix, tempMatrix1)
    tempMatrix2.copyInto(matrix)
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
