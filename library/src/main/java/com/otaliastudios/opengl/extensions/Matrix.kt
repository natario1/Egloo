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

fun FloatArray.translateX(translation: Float): FloatArray {
    checkSize()
    Matrix.translateM(this, 0, translation, 0F, 0F)
    return this
}

fun FloatArray.translateY(translation: Float): FloatArray {
    checkSize()
    Matrix.translateM(this, 0, 0F, translation, 0F)
    return this
}

fun FloatArray.translateZ(translation: Float): FloatArray {
    checkSize()
    Matrix.translateM(this, 0, 0F, 0F, translation)
    return this
}

fun FloatArray.scaleX(scale: Float): FloatArray {
    checkSize()
    Matrix.scaleM(this, 0, scale, 1F, 1F)
    return this
}

fun FloatArray.scaleY(scale: Float): FloatArray {
    checkSize()
    Matrix.scaleM(this, 0, 1F, scale, 1F)
    return this
}

fun FloatArray.scaleZ(scale: Float): FloatArray {
    checkSize()
    Matrix.scaleM(this, 0, 1F, 1F, scale)
    return this
}