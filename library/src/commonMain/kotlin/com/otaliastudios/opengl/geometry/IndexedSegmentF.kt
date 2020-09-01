package com.otaliastudios.opengl.geometry

public class IndexedSegmentF(public val i: Int, public val j: Int, ix: Float, iy: Float, jx: Float, jy: Float)
    : SegmentF(ix, iy, jx, jy) {

    @Suppress("unused")
    public constructor(i: IndexedPointF, j: IndexedPointF) : this(i.index, j.index, i.x, i.y, j.x, j.y)

    override fun intersects(other: SegmentF): Boolean {
        if (other is IndexedSegmentF) {
            if (other.hasIndex(i) && other.hasIndex(j)) return true
            if (other.hasIndex(i) || other.hasIndex(j)) return false
        }
        return super.intersects(other)
    }

    public fun hasIndex(index: Int): Boolean {
        return index == i || index == j
    }
}