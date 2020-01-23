package com.otaliastudios.opengl.draw

import android.graphics.PointF
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.extensions.floatBufferOf
import com.otaliastudios.opengl.extensions.toBuffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import kotlin.IllegalArgumentException
import kotlin.math.*


open class Gl2dMesh: Gl2dDrawable() {

    override var vertexArray: FloatBuffer = floatBufferOf(6)
    private var vertexIndices: ByteBuffer? = null

    private class SegmentF : Comparable<SegmentF> {
        var i: Int = 0
        var j: Int = 0
        var distance: Float = 0F
        var ix: Float = 0F
        var iy: Float = 0F
        var jx: Float = 0F
        var jy: Float = 0F

        override fun compareTo(other: SegmentF): Int {
            // Changing this implementation can change the mesh appearance!
            return distance.compareTo(other.distance)
        }

        /**
         * Not an easy task. Some references:
         * https://github.com/locationtech/jts/blob/master/modules/core/src/main/java/org/locationtech/jts/algorithm/RobustLineIntersector.java
         * https://stackoverflow.com/questions/3838329/how-can-i-check-if-two-segments-intersect
         */
        fun intersects(other: SegmentF): Boolean {
            // Fast checks due to having sorted i and j values. Removable.
            if (i == other.i && j == other.j) return true
            if (i == other.i || j == other.j) return false

            // Check the envelope for a quick fail.
            val thisMinX = min(ix, jx)
            val thisMaxX = max(ix, jx)
            val otherMinX = min(other.ix, other.jx)
            val otherMaxX = max(other.ix, other.jx)
            if (thisMinX > otherMaxX) return false
            if (thisMaxX < otherMinX) return false
            val thisMinY = min(iy, jy)
            val thisMaxY = max(iy, jy)
            val otherMinY = min(other.iy, other.jy)
            val otherMaxY = max(other.iy, other.jy)
            if (thisMinY > otherMaxY) return false
            if (thisMaxY < otherMinY) return false

            // Compute orientations.
            // Fail if relative position is the same (right - right).
            val thisToOtherI = orientation(other.ix, other.iy)
            val thisToOtherJ = orientation(other.jx, other.jy)
            if (thisToOtherI > 0 && thisToOtherJ > 0) return false
            if (thisToOtherI < 0 && thisToOtherJ < 0) return false
            // Same for this with respect to the other
            val otherToThisI = other.orientation(ix, iy)
            val otherToThisJ = other.orientation(jx, jy)
            if (otherToThisI > 0 && otherToThisJ > 0) return false
            if (otherToThisI < 0 && otherToThisJ < 0) return false

            // Check for collinear segments
            if (thisToOtherI == 0 && thisToOtherJ == 0 && otherToThisI == 0 && otherToThisJ == 0) {
                // From first check we know that the two envelopes are intersecting or touching
                // themselves. From the check above we know that segments are collinear.
                // There are 4 adjacency cases. We want to return false because it's a shared point.
                if (thisMinX == otherMaxX && thisMinY == otherMaxY) return false
                if (thisMinX == otherMaxX && thisMaxY == otherMinY) return false
                if (thisMaxX == otherMinX && thisMinY == otherMaxY) return false
                if (thisMaxX == otherMinX && thisMaxY == otherMinY) return false
                return true
            }

            // Envelopes intersect and relative orientations are compatible: we have a single point
            // intersection. However we want to return false if the point is a shared endpoint.
            if (ix == other.ix && iy == other.iy) return false
            if (jx == other.jx && jy == other.jy) return false
            if (ix == other.jx && iy == other.jy) return false
            if (jx == other.ix && jy == other.iy) return false
            return true
        }

        /**
         * Returns -1 if the point is clockwise (right) from us.
         * Returns +1 if the point is counterclockwise (left) from us.
         * 0 if the point is collinear.
         * https://github.com/locationtech/jts/blob/master/modules/core/src/main/java/org/locationtech/jts/algorithm/CGAlgorithmsDD.java#L47-L53
         */
        fun orientation(x: Float, y: Float): Int {
            return ((jx - ix) * (y - jy) - (jy - iy) * (x - jx)).sign.toInt()
        }

        fun hasIndex(index: Int): Boolean {
            return index == i || index == j
        }
    }

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
        val segments = mutableListOf<SegmentF>()
        for (i in 0 until points) {
            val xi = x[i]
            val yi = y[i]
            vertexArray.put(xi)
            vertexArray.put(yi)

            // Compute the distance between this point and all the others.
            for (j in (i + 1) until points) {
                val xj = x[j]
                val yj = y[j]
                segments.add(SegmentF().also {
                    it.i = i
                    it.j = j
                    it.ix = xi
                    it.iy = yi
                    it.jx = xj
                    it.jy = yj
                    it.distance = (xi - xj).pow(2) + (yi - yj).pow(2)
                })
            }
        }
        vertexArray.flip()
        notifyVertexArrayChange()

        // Sort segments and iterate over them to select.
        segments.sort()
        val accepted = mutableListOf<SegmentF>()
        for (s in segments) {
            if (accepted.none { it.intersects(s) }) {
                accepted.add(s)
            }
        }

        // Now we have all segments. Each of this can participate in many triangles,
        // but we actually want to take at most two of them, one on each side.
        // NOTE: consider sorting to make the inner loop faster and to make sure
        // that we take the smaller triangles instead of big ones
        val indices = mutableListOf<Byte>()
        for (si in 0 until accepted.size) {
            val s1 = accepted[si]
            var hasPositiveTriangle = false
            var hasNegativeTriangle = false
            for (sj in (si + 1) until accepted.size) {
                if (hasPositiveTriangle && hasNegativeTriangle) break
                val s2 = accepted[sj]
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
                for (sk in (sj + 1) until accepted.size) {
                    val s3 = accepted[sk]
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