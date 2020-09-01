package com.otaliastudios.opengl.geometry

public expect open class RectF {
    public constructor()
    public constructor(left: Float, top: Float, right: Float, bottom: Float)
    public var left: Float
    public var top: Float
    public var right: Float
    public var bottom: Float
    public fun set(left: Float, top: Float, right: Float, bottom: Float)
}