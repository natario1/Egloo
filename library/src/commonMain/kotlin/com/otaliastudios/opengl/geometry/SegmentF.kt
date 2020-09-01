package com.otaliastudios.opengl.geometry

import kotlin.math.*

public open class SegmentF(public val ix: Float, public val iy: Float, public val jx: Float, public val jy: Float) {

    @Suppress("unused")
    public constructor(i: PointF, j: PointF) : this(i.x, i.y, j.x, j.y)

    public val length: Float by lazy {
        sqrt((ix - jx).pow(2) + (iy - jy).pow(2))
    }

    /**
     * Not an easy task. Some references:
     * https://github.com/locationtech/jts/blob/master/modules/core/src/main/java/org/locationtech/jts/algorithm/RobustLineIntersector.java
     * https://stackoverflow.com/questions/3838329/how-can-i-check-if-two-segments-intersect
     */
    public open fun intersects(other: SegmentF): Boolean {
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
    public fun orientation(x: Float, y: Float): Int {
        return ((jx - ix) * (y - jy) - (jy - iy) * (x - jx)).sign.toInt()
    }
}