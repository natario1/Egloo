package com.otaliastudios.opengl.geometry

public actual open class RectF actual constructor(
        public actual var left: Float,
        public actual var top: Float,
        public actual var right: Float,
        public actual var bottom: Float) {
    public actual constructor() : this(0F, 0F, 0F, 0F)
    public actual fun set(left: Float, top: Float, right: Float, bottom: Float) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }
}