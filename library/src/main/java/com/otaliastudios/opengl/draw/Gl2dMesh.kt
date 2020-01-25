package com.otaliastudios.opengl.draw

import android.graphics.PointF
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.extensions.floatBufferOf
import com.otaliastudios.opengl.extensions.toBuffer
import com.otaliastudios.opengl.geometry.IndexedSegmentF
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import kotlin.IllegalArgumentException
import kotlin.math.*


open class Gl2dMesh: Gl2dDrawable() {

    override var vertexArray: FloatBuffer = floatBufferOf(6)
    private var vertexIndices: ByteBuffer? = null

    fun setPoints(points: List<PointF>) {
        setPoints(points.map { it.x }, points.map { it.y })
    }

    fun setPoints(x: List<Float>, y: List<Float>) {
        if (x.size != y.size) throw IllegalArgumentException("x.size != y.size")
        val points = x.size
        val coords = points * 2
        if (vertexArray.capacity() < coords) {
            vertexArray = floatBufferOf(coords)
        } else {
            vertexArray.clear()
        }
        val segments = mutableListOf<IndexedSegmentF>()
        for (i in 0 until points) {
            val xi = x[i]
            val yi = y[i]
            vertexArray.put(xi)
            vertexArray.put(yi)
            for (j in (i + 1) until points) {
                val xj = x[j]
                val yj = y[j]
                segments.add(IndexedSegmentF(i, j, xi, yi, xj, yj))
            }
        }
        vertexArray.flip()
        notifyVertexArrayChange()

        // Sort segments and iterate over them to select.
        segments.sortBy { it.length }
        val accepted = mutableListOf<IndexedSegmentF>()
        for (s in segments) {
            if (accepted.none { it.intersects(s) }) {
                accepted.add(s)
            }
        }
        computeIndicesFromIndexedSegments(accepted)
    }

    // Segments must be non intersecting and sorted by ascending length
    private fun computeIndicesFromIndexedSegments(segments: List<IndexedSegmentF>) {
        // Now we have all segments. Each of this can participate in many triangles,
        // but we actually want to take at most two of them, one on each side.
        // NOTE: consider sorting to make the inner loop faster and to make sure
        // that we take the smaller triangles instead of big ones
        val indices = mutableListOf<Byte>()
        for (si in 0 until segments.size) {
            val s1 = segments[si]
            var hasPositiveTriangle = false
            var hasNegativeTriangle = false
            for (sj in (si + 1) until segments.size) {
                if (hasPositiveTriangle && hasNegativeTriangle) break
                val s2 = segments[sj]
                var s2UnsharedIndex: Int
                var s2UnsharedX: Float
                var s2UnsharedY: Float
                if (s1.hasIndex(s2.i)) {
                    // s2i is the shared value! s2j is the other.
                    s2UnsharedIndex = s2.j
                    s2UnsharedX = s2.jx
                    s2UnsharedY = s2.jy
                } else if (s1.hasIndex(s2.j)) {
                    // s2j is the shared value! s2i is the other.
                    s2UnsharedIndex = s2.i
                    s2UnsharedX = s2.ix
                    s2UnsharedY = s2.iy
                } else {
                    // Keep searching
                    continue
                }
                // Since we have two segments, and they are sorted by length, in theory we don't
                // need to search for the last one, it MUST exist. We could assume it does.
                // However this might create duplicate triangles when going on with the outer loop.
                // I don't think we should search for the last one. It MUST exist.
                // So we could simply create it.
                val orientation = s1.orientation(s2UnsharedX, s2UnsharedY)
                if (orientation == 0) continue
                if (orientation > 0 && hasPositiveTriangle) continue
                if (orientation < 0 && hasNegativeTriangle) continue
                for (sk in (sj + 1) until segments.size) {
                    val s3 = segments[sk]
                    if (s3.hasIndex(s2UnsharedIndex) && (s3.hasIndex(s1.i) || s3.hasIndex(s1.j))) {
                        // Shares a point with s1, and shares the correct point with s2.
                        indices.add(s1.i.toByte())
                        indices.add(s1.j.toByte())
                        indices.add(s2UnsharedIndex.toByte())
                        if (orientation > 0) hasPositiveTriangle = true
                        if (orientation < 0) hasNegativeTriangle = true
                        break
                    }
                }
            }
        }
        vertexIndices = indices.toByteArray().toBuffer()
    }

    override fun draw() {
        vertexIndices?.let {
            Egloo.checkGlError("glDrawElements start")
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, it.limit(), GLES20.GL_UNSIGNED_BYTE, it)
            Egloo.checkGlError("glDrawElements end")
        }
    }
}