package com.otaliastudios.opengl.draw


import com.otaliastudios.opengl.core.Egloo
import java.nio.FloatBuffer

/**
 * This interface can be used by programs to let the drawable provide the texture
 * coordinates.
 */
interface GlTexturable {

    /**
     * Returns the array of texture coordinates that will be mapped to the drawable's
     * vertex coordinates.
     */
    val textureCoordsArray: FloatBuffer

    /**
     * Returns the number of texture coordinates per vertex.
     */
    val textureCoordsPerVertex: Int

    /**
     * Number of bytes for each texture coordinate.
     */
    val textureCoordsStride get() = textureCoordsPerVertex * Egloo.SIZE_OF_FLOAT
}