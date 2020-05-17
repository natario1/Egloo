package com.otaliastudios.opengl.extensions

import com.otaliastudios.opengl.internal.matrixMakeIdentity
import com.otaliastudios.opengl.internal.matrixRotate
import com.otaliastudios.opengl.internal.matrixScale
import com.otaliastudios.opengl.internal.matrixTranslate

private fun FloatArray.checkSize() {
    if (size != 16) throw RuntimeException("Need a 16 values matrix.")
}

fun FloatArray.makeIdentity(): FloatArray {
    checkSize()
    matrixMakeIdentity(this)
    return this
}

fun FloatArray.clear(): FloatArray {
    return makeIdentity()
}

fun FloatArray.translate(x: Float = 0F, y: Float = 0F, z: Float = 0F): FloatArray {
    checkSize()
    matrixTranslate(this, x, y, z)
    return this
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
    matrixScale(this, x, y, z)
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

fun FloatArray.rotate(angle: Float, x: Float, y: Float, z: Float): FloatArray {
    checkSize()
    matrixRotate(this, angle, x, y, z)
    return this
}

fun FloatArray.rotateX(angle: Float): FloatArray {
    return rotate(angle = angle, x = 1F, y = 0F, z = 0F)
}


fun FloatArray.rotateY(angle: Float): FloatArray {
    return rotate(angle = angle, x = 0F, y = 1F, z = 0F)
}


fun FloatArray.rotateZ(angle: Float): FloatArray {
    return rotate(angle = angle, x = 0F, y = 0F, z = 1F)
}
