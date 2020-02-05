package com.otaliastudios.opengl.geometry

data class Rect3F(
        var left: Float, var top: Float, var near: Float,
        var right: Float, var bottom: Float, var far: Float) {

    constructor() : this(0F, 0F, 0F, 0F, 0F, 0F)

    constructor(other: Rect3F) : this(other.left, other.top, other.near,
            other.right, other.bottom, other.far)

    fun set(left: Float, top: Float, near: Float, right: Float, bottom: Float, far: Float) {
        this.left = left
        this.top = top
        this.near = near
        this.right = right
        this.bottom = bottom
        this.far = far
    }
}