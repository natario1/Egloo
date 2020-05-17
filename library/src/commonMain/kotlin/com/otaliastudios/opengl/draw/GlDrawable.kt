package com.otaliastudios.opengl.draw


import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.core.GlViewportAware
import com.otaliastudios.opengl.types.FloatBuffer
import com.otaliastudios.opengl.types.dispose
import com.otaliastudios.opengl.internal.matrixClone

abstract class GlDrawable : GlViewportAware() {

    /**
     * The model matrix for this object. Defaults to the
     * identity matrix, but can be accessed and modified.
     */
    val modelMatrix = matrixClone(Egloo.IDENTITY_MATRIX)

    /**
     * Returns the array of vertices.
     * To avoid allocations, this returns internal state. The caller must not modify it.
     */
    // TODO if this is set, like vertexArray = ..., we won't call Buffer.dispose().
    abstract var vertexArray: FloatBuffer

    /**
     * Returns the number of position coordinates per vertex. This will be 2 or 3.
     */
    abstract val coordsPerVertex: Int

    /**
     * Returns the width, in bytes, of the data for each vertex.
     */
    open val vertexStride: Int
        get() = coordsPerVertex * Egloo.SIZE_OF_FLOAT

    /**
     * Returns the number of vertices stored in the vertex array.
     */
    open val vertexCount: Int
        get() = vertexArray.limit() / coordsPerVertex

    /**
     * Draws this drawable.
     * This function should not be called directly.
     * Instead, this drawable should be passed to some [GlProgram].
     */
    abstract fun draw()

    var vertexArrayVersion: Int = 0
        private set

    protected fun notifyVertexArrayChange() {
        vertexArrayVersion++
    }

    open fun release() {
        vertexArray.dispose()
    }
}