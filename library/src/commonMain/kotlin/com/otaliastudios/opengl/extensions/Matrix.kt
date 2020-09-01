package com.otaliastudios.opengl.extensions

import com.otaliastudios.opengl.internal.matrixMakeIdentity
import com.otaliastudios.opengl.internal.matrixRotate
import com.otaliastudios.opengl.internal.matrixScale
import com.otaliastudios.opengl.internal.matrixTranslate

private fun FloatArray.checkSize() {
    if (size != 16) throw RuntimeException("Need a 16 values matrix.")
}

public fun FloatArray.makeIdentity(): FloatArray {
    checkSize()
    matrixMakeIdentity(this)
    return this
}

public fun FloatArray.clear(): FloatArray {
    return makeIdentity()
}

public fun FloatArray.translate(x: Float = 0F, y: Float = 0F, z: Float = 0F): FloatArray {
    checkSize()
    matrixTranslate(this, x, y, z)
    return this
}

@Suppress("unused")
public fun FloatArray.translateX(translation: Float): FloatArray {
    return translate(x = translation)
}

@Suppress("unused")
public fun FloatArray.translateY(translation: Float): FloatArray {
    return translate(y = translation)
}

@Suppress("unused")
public fun FloatArray.translateZ(translation: Float): FloatArray {
    return translate(z = translation)
}

public fun FloatArray.scale(x: Float = 1F, y: Float = 1F, z: Float = 1F): FloatArray {
    checkSize()
    matrixScale(this, x, y, z)
    return this
}

public fun FloatArray.scaleX(scale: Float): FloatArray {
    return scale(x = scale)
}

public fun FloatArray.scaleY(scale: Float): FloatArray {
    return scale(y = scale)
}

@Suppress("unused")
public fun FloatArray.scaleZ(scale: Float): FloatArray {
    return scale(z = scale)
}

public fun FloatArray.rotate(angle: Float, x: Float, y: Float, z: Float): FloatArray {
    checkSize()
    matrixRotate(this, angle, x, y, z)
    return this
}

public fun FloatArray.rotateX(angle: Float): FloatArray {
    return rotate(angle = angle, x = 1F, y = 0F, z = 0F)
}


public fun FloatArray.rotateY(angle: Float): FloatArray {
    return rotate(angle = angle, x = 0F, y = 1F, z = 0F)
}


public fun FloatArray.rotateZ(angle: Float): FloatArray {
    return rotate(angle = angle, x = 0F, y = 0F, z = 1F)
}
