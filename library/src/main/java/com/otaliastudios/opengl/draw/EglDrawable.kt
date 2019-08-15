package com.otaliastudios.opengl.draw


import com.otaliastudios.opengl.core.Egl
import com.otaliastudios.opengl.program.EglProgram
import java.nio.FloatBuffer

abstract class EglDrawable {

    /**
     * The model matrix for this object. Defaults to the
     * identity matrix, but can be accessed and modified.
     */
    val modelMatrix = Egl.IDENTITY_MATRIX.clone()

    /**
     * Returns the array of vertices.
     * To avoid allocations, this returns internal state.  The caller must not modify it.
     */
    abstract var vertexArray: FloatBuffer

    /**
     * Returns the number of position coordinates per vertex.  This will be 2 or 3.
     */
    abstract val coordsPerVertex: Int

    /**
     * Returns the width, in bytes, of the data for each vertex.
     */
    open val vertexStride: Int
        get() = coordsPerVertex * Egl.SIZE_OF_FLOAT

    /**
     * Returns the number of vertices stored in the vertex array.
     */
    open val vertexCount: Int
        get() = vertexArray.capacity() / coordsPerVertex

    /**
     * Draws this drawable.
     * This function should not be called directly.
     * Instead, this drawable should be passed to some [EglProgram].
     */
    abstract fun draw()
}