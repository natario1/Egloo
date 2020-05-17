package com.otaliastudios.opengl.core

import com.otaliastudios.opengl.internal.EGL_CONTEXT_CLIENT_VERSION
import com.otaliastudios.opengl.internal.EGL_DRAW
import com.otaliastudios.opengl.internal.EGL_HEIGHT
import com.otaliastudios.opengl.internal.EGL_NONE
import com.otaliastudios.opengl.internal.EGL_NO_CONTEXT
import com.otaliastudios.opengl.internal.EGL_NO_DISPLAY
import com.otaliastudios.opengl.internal.EGL_NO_SURFACE
import com.otaliastudios.opengl.internal.EGL_WIDTH
import com.otaliastudios.opengl.internal.EglConfig
import com.otaliastudios.opengl.internal.EglContext
import com.otaliastudios.opengl.internal.EglDisplay
import com.otaliastudios.opengl.internal.EglSurface
import com.otaliastudios.opengl.internal.eglCreateContext
import com.otaliastudios.opengl.internal.eglCreatePbufferSurface
import com.otaliastudios.opengl.internal.eglCreateWindowSurface
import com.otaliastudios.opengl.internal.eglDestroyContext
import com.otaliastudios.opengl.internal.eglDestroySurface
import com.otaliastudios.opengl.internal.eglGetCurrentContext
import com.otaliastudios.opengl.internal.eglGetCurrentSurface
import com.otaliastudios.opengl.internal.eglGetDefaultDisplay
import com.otaliastudios.opengl.internal.eglInitialize
import com.otaliastudios.opengl.internal.eglMakeCurrent
import com.otaliastudios.opengl.internal.eglPresentationTime
import com.otaliastudios.opengl.internal.eglQuerySurface
import com.otaliastudios.opengl.internal.eglReleaseThread
import com.otaliastudios.opengl.internal.eglSwapBuffers
import com.otaliastudios.opengl.internal.eglTerminate
import com.otaliastudios.opengl.internal.logv

expect class EglCore : EglNativeCore

open class EglNativeCore internal constructor(sharedContext: EglContext = EGL_NO_CONTEXT, flags: Int = 0) {

    private var eglDisplay: EglDisplay = EGL_NO_DISPLAY
    private var eglContext: EglContext = EGL_NO_CONTEXT
    private var eglConfig: EglConfig? = null
    private var glVersion = -1 // 2 or 3

    init {
        eglDisplay = eglGetDefaultDisplay()
        if (eglDisplay === EGL_NO_DISPLAY) {
            throw RuntimeException("unable to get EGL14 display")
        }

        if (!eglInitialize(eglDisplay, IntArray(1), IntArray(1))) {
            throw RuntimeException("unable to initialize EGL14")
        }

        // Try to get a GLES3 context, if requested.
        val chooser = EglNativeConfigChooser()
        val recordable = flags and FLAG_RECORDABLE != 0
        val tryGles3 = flags and FLAG_TRY_GLES3 != 0
        if (tryGles3) {
            val config = chooser.getConfig(eglDisplay, 3, recordable)
            if (config != null) {
                val attributes = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 3, EGL_NONE)
                val context = eglCreateContext(eglDisplay, config, sharedContext, attributes)
                try {
                    Egloo.checkEglError("eglCreateContext (3)")
                    eglConfig = config
                    eglContext = context
                    glVersion = 3
                } catch (e: Exception) {
                    // Swallow, will try GLES2
                }
            }
        }

        // If GLES3 failed, go with GLES2.
        val tryGles2 = eglContext === EGL_NO_CONTEXT
        if (tryGles2) {
            val config = chooser.getConfig(eglDisplay, 2, recordable)
            if (config != null) {
                val attributes = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE)
                val context = eglCreateContext(eglDisplay, config, sharedContext, attributes)
                Egloo.checkEglError("eglCreateContext (2)")
                eglConfig = config
                eglContext = context
                glVersion = 2
            } else {
                throw RuntimeException("Unable to find a suitable EGLConfig")
            }
        }
    }

    /**
     * Discards all resources held by this class, notably the EGL context.  This must be
     * called from the thread where the context was created.
     * On completion, no context will be current.
     */
    internal open fun release() {
        if (eglDisplay !== EGL_NO_DISPLAY) {
            // Android is unusual in that it uses a reference-counted EGLDisplay.  So for
            // every eglInitialize() we need an eglTerminate().
            eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT)
            eglDestroyContext(eglDisplay, eglContext)
            eglReleaseThread()
            eglTerminate(eglDisplay)
        }
        eglDisplay = EGL_NO_DISPLAY
        eglContext = EGL_NO_CONTEXT
        eglConfig = null
    }

    /**
     * Makes this context current, with no read / write surfaces.
     */
    internal open fun makeCurrent() {
        if (!eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, eglContext)) {
            throw RuntimeException("eglMakeCurrent failed")
        }
    }

    /**
     * Destroys the specified surface.  Note the EGLSurface won't actually be destroyed if it's
     * still current in a context.
     */
    internal fun releaseSurface(eglSurface: EglSurface) {
        eglDestroySurface(eglDisplay, eglSurface)
    }

    /**
     * Creates an EGL surface associated with a Surface.
     * If this is destined for MediaCodec, the EGLConfig should have the "recordable" attribute.
     */
    internal fun createWindowSurface(surface: Any): EglSurface {
        // Create a window surface, and attach it to the Surface we received.
        val surfaceAttribs = intArrayOf(EGL_NONE)
        val eglSurface = eglCreateWindowSurface(eglDisplay, eglConfig!!, surface, surfaceAttribs)
        Egloo.checkEglError("eglCreateWindowSurface")
        if (eglSurface === EGL_NO_SURFACE) throw RuntimeException("surface was null")
        return eglSurface
    }

    /**
     * Creates an EGL surface associated with an offscreen buffer.
     */
    internal fun createOffscreenSurface(width: Int, height: Int): EglSurface {
        val surfaceAttribs = intArrayOf(EGL_WIDTH, width, EGL_HEIGHT, height, EGL_NONE)
        val eglSurface = eglCreatePbufferSurface(eglDisplay, eglConfig!!, surfaceAttribs)
        Egloo.checkEglError("eglCreatePbufferSurface")
        if (eglSurface === EGL_NO_SURFACE) throw RuntimeException("surface was null")
        return eglSurface
    }

    /**
     * Makes our EGL context current, using the supplied surface for both "draw" and "read".
     */
    internal fun makeSurfaceCurrent(eglSurface: EglSurface) {
        if (eglDisplay === EGL_NO_DISPLAY) logv("EglCore", "NOTE: makeSurfaceCurrent w/o display")
        if (!eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw RuntimeException("eglMakeCurrent failed")
        }
    }

    /**
     * Makes our EGL context current, using the supplied "draw" and "read" surfaces.
     */
    internal fun makeSurfaceCurrent(drawSurface: EglSurface, readSurface: EglSurface) {
        if (eglDisplay === EGL_NO_DISPLAY) logv("EglCore", "NOTE: makeSurfaceCurrent w/o display")
        if (!eglMakeCurrent(eglDisplay, drawSurface, readSurface, eglContext)) {
            throw RuntimeException("eglMakeCurrent(draw,read) failed")
        }
    }

    /**
     * Calls eglSwapBuffers. Use this to "publish" the current frame.
     * @return false on failure
     */
    internal fun swapSurfaceBuffers(eglSurface: EglSurface): Boolean {
        return eglSwapBuffers(eglDisplay, eglSurface)
    }

    /**
     * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
     */
    internal fun setSurfacePresentationTime(eglSurface: EglSurface, nsecs: Long) {
        eglPresentationTime(eglDisplay, eglSurface, nsecs)
    }

    /**
     * Returns true if our context and the specified surface are current.
     */
    internal fun isSurfaceCurrent(eglSurface: EglSurface): Boolean {
        return eglContext == eglGetCurrentContext()
                && eglSurface == eglGetCurrentSurface(EGL_DRAW)
    }

    /**
     * Performs a simple surface query.
     */
    internal fun querySurface(eglSurface: EglSurface, what: Int): Int {
        val value = IntArray(1)
        eglQuerySurface(eglDisplay, eglSurface, what, value)
        return value[0]
    }

    companion object {
        /**
         * Constructor flag: surface must be recordable.  This discourages EGL from using a
         * pixel format that cannot be converted efficiently to something usable by the video
         * encoder.
         */
        internal const val FLAG_RECORDABLE = 0x01

        /**
         * Constructor flag: ask for GLES3, fall back to GLES2 if not available.  Without this
         * flag, GLES2 is used.
         */
        internal const val FLAG_TRY_GLES3 = 0x02
    }
}