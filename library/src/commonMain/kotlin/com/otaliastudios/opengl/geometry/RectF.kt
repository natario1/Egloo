package com.otaliastudios.opengl.geometry

expect open class RectF {
    constructor()
    constructor(left: Float, top: Float, right: Float, bottom: Float)
    var left: Float
    var top: Float
    var right: Float
    var bottom: Float
    fun set(left: Float, top: Float, right: Float, bottom: Float)
}