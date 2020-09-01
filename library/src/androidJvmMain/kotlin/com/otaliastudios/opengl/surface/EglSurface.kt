package com.otaliastudios.opengl.surface


import android.graphics.Bitmap
import android.opengl.EGLSurface
import android.opengl.GLES20
import com.otaliastudios.opengl.core.Egloo
import com.otaliastudios.opengl.core.EglCore
import com.otaliastudios.opengl.internal.EglSurface
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.jvm.Throws

/**
 * Common base class for EGL surfaces.
 * There can be multiple base surfaces associated with a single [EglCore] object.
 */
public actual abstract class EglSurface internal actual constructor(
        eglCore: EglCore,
        eglSurface: EglSurface
) : EglNativeSurface(eglCore, eglSurface) {

    @Suppress("unused")
    protected constructor(eglCore: EglCore, eglSurface: EGLSurface)
            : this(eglCore, EglSurface(eglSurface))

    /**
     * Saves the EGL surface to the given output stream.
     * Expects that this object's EGL surface is current.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun toOutputStream(stream: OutputStream, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG) {
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
        Egloo.checkGlError("glReadPixels")
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
    public fun toFile(file: File, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG) {
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
    public fun toByteArray(format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray {
        val stream = ByteArrayOutputStream()
        stream.use {
            toOutputStream(it, format)
            return it.toByteArray()
        }
    }

    public companion object {
        @Suppress("HasPlatformType", "unused")
        protected val TAG: String = EglSurface::class.java.simpleName
    }
}