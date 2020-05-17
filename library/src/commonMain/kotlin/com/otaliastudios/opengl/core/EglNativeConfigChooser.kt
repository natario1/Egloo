package com.otaliastudios.opengl.core

import com.otaliastudios.opengl.internal.EGL_ALPHA_SIZE
import com.otaliastudios.opengl.internal.EGL_BLUE_SIZE
import com.otaliastudios.opengl.internal.EGL_GREEN_SIZE
import com.otaliastudios.opengl.internal.EGL_NONE
import com.otaliastudios.opengl.internal.EGL_OPENGL_ES2_BIT
import com.otaliastudios.opengl.internal.EGL_OPENGL_ES3_BIT_KHR
import com.otaliastudios.opengl.internal.EGL_PBUFFER_BIT
import com.otaliastudios.opengl.internal.EGL_RED_SIZE
import com.otaliastudios.opengl.internal.EGL_RENDERABLE_TYPE
import com.otaliastudios.opengl.internal.EGL_SURFACE_TYPE
import com.otaliastudios.opengl.internal.EGL_WINDOW_BIT
import com.otaliastudios.opengl.internal.EglConfig
import com.otaliastudios.opengl.internal.EglDisplay
import com.otaliastudios.opengl.internal.eglChooseConfig
import com.otaliastudios.opengl.internal.logw

open class EglNativeConfigChooser {

    companion object {
        private const val EGL_RECORDABLE_ANDROID = 0x3142 // Android-specific extension.
    }

    internal fun getConfig(display: EglDisplay, version: Int, recordable: Boolean): EglConfig? {
        val attributes = getConfigSpec(version, recordable)
        val configs = arrayOfNulls<EglConfig?>(1)
        val numConfigs = IntArray(1)
        if (!eglChooseConfig(display, attributes, configs, 1, numConfigs)) {
            logw("EglConfigChooser", "Unable to find RGB8888 / $version EGLConfig")
            return null
        }
        return configs.get(0)
    }

    /**
     * Finds a suitable EGLConfig with r=8, g=8, b=8, a=8.
     * Does not specify depth or stencil size.
     *
     * The actual drawing surface is generally RGBA or RGBX, so omitting the alpha doesn't
     * really help - it can also lead to huge performance hit on glReadPixels() when reading
     * into a GL_RGBA buffer.
     */
    internal fun getConfigSpec(version: Int, recordable: Boolean): IntArray {
        val renderableType = if (version >= 3) {
            EGL_OPENGL_ES2_BIT or EGL_OPENGL_ES3_BIT_KHR
        } else {
            EGL_OPENGL_ES2_BIT
        }
        return intArrayOf(
                EGL_RED_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_ALPHA_SIZE, 8,
                // We can create both window surfaces and pbuffer surfaces.
                EGL_SURFACE_TYPE, EGL_WINDOW_BIT or EGL_PBUFFER_BIT,
                EGL_RENDERABLE_TYPE, renderableType,
                if (recordable) EGL_RECORDABLE_ANDROID else EGL_NONE,
                if (recordable) 1 else 0,
                EGL_NONE)
    }
}