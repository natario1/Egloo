package com.otaliastudios.opengl.draw

import kotlin.math.sqrt

@Suppress("unused")
public open class GlSquare : GlPolygon(4) {
    init {
        // We expect a square to be horizontal, which is not what GlPolygon does.
        // We compensate here and compensate for the radius.
        rotation = 45F
        radius = sqrt(2F)
    }
}