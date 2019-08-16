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
    return this;
}

@Suppress("unused")
fun FloatArray.translateX(translation: Float): FloatArray {
    return translate(x = translation)
}

@Suppress("unused")
fun FloatArray.translateY(translation: Float): FloatArray {
    return translate(y = translation)
}

@Suppress("unused")
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

@Suppress("unused")
fun FloatArray.scaleZ(scale: Float): FloatArray {
    return scale(z = scale)

}