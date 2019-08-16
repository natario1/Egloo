package com.otaliastudios.opengl.surface


import android.graphics.Bitmap
import android.opengl.EGL14
import android.opengl.EGLSurface
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egl
import com.otaliastudios.opengl.core.EglCore
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Common base class for EGL surfaces.
 * There can be multiple base surfaces associated with a single [EglCore] object.
 */
abstract class EglSurface protected constructor(
        protected var eglCore: EglCore,
        protected var eglSurface: EGLSurface) {

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
    fun getWidth(): Int {
        return if (width < 0) {
            eglCore.querySurface(eglSurface, EGL14.EGL_WIDTH)
        } else {
            width
        }
    }

    /**
     * Returns the surface's height, in pixels.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getHeight(): Int {
        return if (height < 0) {
            eglCore.querySurface(eglSurface, EGL14.EGL_HEIGHT)
        } else {
            height
        }
    }

    /**
     * Release the EGL surface.
     */
    open fun release() {
        eglCore.releaseSurface(eglSurface)
        eglSurface = EGL14.EGL_NO_SURFACE
        height = -1
        width = -1
    }

    /**
     * Whether this surface is current on the
     * attached [EglCore].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun isCurrent(): Boolean {
        return eglCore.isSurfaceCurrent(eglSurface)
    }

    /**
     * Makes our EGL context and surface current.
     */
    @Suppress("unused")
    fun makeCurrent() {
        eglCore.makeSurfaceCurrent(eglSurface)
    }

    /**
     * Makes our EGL context and surface current for drawing,
     * using the supplied surface for reading.
     */
    @Suppress("unused")
    fun makeCurrent(readSurface: EglSurface) {
        eglCore.makeSurfaceCurrent(eglSurface, readSurface.eglSurface)
    }

    /**
     * Makes no surface current for the attached [eglCore].
     */
    @Suppress("unused")
    fun makeNothingCurrent() {
        eglCore.makeNoSurfaceCurrent()
    }

    /**
     * Sends the presentation time stamp to EGL.
     * [nsecs] is the timestamp in nanoseconds.
     */
    @Suppress("unused")
    fun setPresentationTime(nsecs: Long) {
        eglCore.setSurfacePresentationTime(eglSurface, nsecs)
    }

    /**
     * Saves the EGL surface to the given output stream.
     * Expects that this object's EGL surface is current.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun toOutputStream(stream: OutputStream, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG) {
        if (!isCurrent()) throw RuntimeException("Expected EGL context/surface is not current")
        // glReadPixels fills in a "direct" ByteBuffer with what is essentially big-endian RGBA
        // data (i.e. a byte of red, followed by a byte of green...).  While the Bitmap
        // constructor that takes an int[] wants little-endian ARGB (blue/red swapped), the
        // Bitmap "copy pixels" method wants the same format GL provides.
        //
        // Making this even more interesting is the upside-down nature of GL, which means
        // our output will look upside down relative to what appears on screen if the
        // typical GL conventions are used.
        val width = getWidth()
        val height = getHeight()
        val buf = ByteBuffer.allocateDirect(width * height * 4)
        buf.order(ByteOrder.LITTLE_ENDIAN)
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf)
        Egl.checkGlError("glReadPixels")
        buf.rewind()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buf)
        bitmap.compress(format, 90, stream)
        bitmap.recycle()
    }

    /**
     * Saves the EGL surface to a file.
     * Expects that this object's EGL surface is current.
     */
    @Suppress("unused")
    @Throws(IOException::class)
    fun toFile(file: File, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG) {
        var stream: BufferedOutputStream? = null
        try {
            stream = BufferedOutputStream(FileOutputStream(file.toString()))
            toOutputStream(stream, format)
        } finally {
            stream?.close()
        }
    }

    /**
     * Saves the EGL surface to given format.
     * Expects that this object's EGL surface is current.
     */
    @Suppress("unused")
    fun toByteArray(format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray {
        val stream = ByteArrayOutputStream()
        stream.use {
            toOutputStream(it, format)
            return it.toByteArray()
        }
    }

    companion object {
        @Suppress("HasPlatformType", "unused")
        protected val TAG = EglSurface::class.java.simpleName
    }
}