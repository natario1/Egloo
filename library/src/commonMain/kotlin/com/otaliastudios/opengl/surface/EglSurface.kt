package com.otaliastudios.opengl.surface

import com.otaliastudios.opengl.core.EglCore
import com.otaliastudios.opengl.internal.EGL_HEIGHT
import com.otaliastudios.opengl.internal.EGL_NO_SURFACE
import com.otaliastudios.opengl.internal.EGL_WIDTH
import com.otaliastudios.opengl.internal.EglSurface

public expect abstract class EglSurface internal constructor(eglCore: EglCore, eglSurface: EglSurface) : EglNativeSurface

public open class EglNativeSurface internal constructor(
        internal var eglCore: EglCore,
        internal var eglSurface: EglSurface) {

    private var width = -1
    private var height = -1

    /**
     * Can be called by subclasses whose width is guaranteed to never change,
     * so we can cache this value. For window surfaces, this should not be called.
     */
    @Suppress("unused")
    protected fun setWidth(width: Int) {
        this.width = width
    }

    /**
     * Can be called by subclasses whose height is guaranteed to never change,
     * so we can cache this value. For window surfaces, this should not be called.
     */
    @Suppress("unused")
    protected fun setHeight(height: Int) {
        this.height = height
    }

    /**
     * Returns the surface's width, in pixels.
     *
     * If this is called on a window surface, and the underlying surface is in the process
     * of changing size, we may not see the new size right away (e.g. in the "surfaceChanged"
     * callback).  The size should match after the next buffer swap.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun getWidth(): Int {
        return if (width < 0) {
            eglCore.querySurface(eglSurface, EGL_WIDTH)
        } else {
            width
        }
    }

    /**
     * Returns the surface's height, in pixels.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun getHeight(): Int {
        return if (height < 0) {
            eglCore.querySurface(eglSurface, EGL_HEIGHT)
        } else {
            height
        }
    }

    /**
     * Release the EGL surface.
     */
    public open fun release() {
        eglCore.releaseSurface(eglSurface)
        eglSurface = EGL_NO_SURFACE
        height = -1
        width = -1
    }

    /**
     * Whether this surface is current on the
     * attached [EglCore].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun isCurrent(): Boolean {
        return eglCore.isSurfaceCurrent(eglSurface)
    }

    /**
     * Makes our EGL context and surface current.
     */
    @Suppress("unused")
    public fun makeCurrent() {
        eglCore.makeSurfaceCurrent(eglSurface)
    }

    /**
     * Makes no surface current for the attached [eglCore].
     */
    @Suppress("unused")
    public fun makeNothingCurrent() {
        eglCore.makeCurrent()
    }

    /**
     * Sends the presentation time stamp to EGL.
     * [nsecs] is the timestamp in nanoseconds.
     */
    @Suppress("unused")
    public fun setPresentationTime(nsecs: Long) {
        eglCore.setSurfacePresentationTime(eglSurface, nsecs)
    }
}