@file:Suppress("unused")

package com.otaliastudios.opengl.extensions

import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

private fun FloatArray.checkSize() {
    if (size != 16) throw RuntimeException("Need a 16 values matrix.")
}

fun FloatArray.makeIdentity(): FloatArray {
    checkSize()
    Matrix.setIdentityM(this, 0)
    return this
}

fun FloatArray.clear(): FloatArray {
    return makeIdentity()
}

fun FloatArray.translate(x: Float = 0F, y: Float = 0F, z: Float = 0F): FloatArray {
    checkSize()
    Matrix.translateM(this, 0, x, y, z)
    return this
}

fun FloatArray.translateX(translation: Float): FloatArray {
    return translate(x = translation)
}

fun FloatArray.translateY(translation: Float): FloatArray {
    return translate(y = translation)
}

fun FloatArray.translateZ(translation: Float): FloatArray {
    return translate(z = translation)
}

fun FloatArray.scale(x: Float = 1F, y: Float = 1F, z: Float = 1F): FloatArray {
    checkSize()
    Matrix.scaleM(this, 0, x, y, z)
    return this
}

fun FloatArray.scaleX(scale: Float): FloatArray {
    return scale(x = scale)
}

fun FloatArray.scaleY(scale: Float): FloatArray {
    return scale(y = scale)
}

fun FloatArray.scaleZ(scale: Float): FloatArray {
    return scale(z = scale)
}

fun FloatArray.rotate(x: Float = 0F, y: Float = 0F, z: Float = 0F): FloatArray {
    checkSize()
    if (x != 0F) Matrix.rotateM(this, 0, x, 1F, 0F, 0F)
    if (y != 0F) Matrix.rotateM(this, 0, y, 0F, 1F, 0F)
    if (z != 0F) Matrix.rotateM(this, 0, z, 0F, 0F, 1F)
    return this
}

fun FloatArray.rotateX(rotation: Float): FloatArray {
    return rotate(x = rotation)
}

fun FloatArray.rotateY(rotation: Float): FloatArray {
    return rotate(y = rotation)
}

fun FloatArray.rotateZ(rotation: Float): FloatArray {
    return rotate(z = rotation)
}