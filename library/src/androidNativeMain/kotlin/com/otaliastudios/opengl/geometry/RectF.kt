package com.otaliastudios.opengl.geometry

actual open class RectF actual constructor(
        actual var left: Float,
        actual var top: Float,
        actual var right: Float,
        actual var bottom: Float) {
    actual constructor() : this(0F, 0F, 0F, 0F)
    actual fun set(left: Float, top: Float, right: Float, bottom: Float) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }
}