package com.otaliastudios.opengl.core

import android.opengl.EGL14
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay

/**
 * Helper for [GLSurfaceView.setEGLContextFactory].
 */
@Suppress("unused")
public object EglContextFactory {
    private val TAG = EglContextFactory::class.java.simpleName

    @Suppress("unused")
    @JvmField
    public val GLES2: GLSurfaceView.EGLContextFactory = Factory(2)

    @Suppress("unused")
    @JvmField
    public val GLES3: GLSurfaceView.EGLContextFactory = Factory(3)

    private class Factory(private val version: Int) : GLSurfaceView.EGLContextFactory {
        override fun createContext(egl: EGL10, display: EGLDisplay, eglConfig: EGLConfig): EGLContext {
            val attributes = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, version, EGL14.EGL_NONE)
            return egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attributes)
        }

        override fun destroyContext(egl: EGL10, display: EGLDisplay, context: EGLContext) {
            if (!egl.eglDestroyContext(display, context)) {
                Log.e(TAG, "display:$display context:$context")
                throw RuntimeException("eglDestroyContex" + egl.eglGetError())
            }
        }
    }
}