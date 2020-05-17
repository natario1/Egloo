package com.otaliastudios.opengl.surface

import com.otaliastudios.opengl.core.EglCore
import com.otaliastudios.opengl.internal.EGL_HEIGHT
import com.otaliastudios.opengl.internal.EGL_NO_SURFACE
import com.otaliastudios.opengl.internal.EGL_WIDTH
import com.otaliastudios.opengl.internal.EglSurface

expect open class EglWindowSurface : EglNativeWindowSurface

abstract class EglNativeWindowSurface internal constructor(
        eglCore: EglCore,
        eglSurface: EglSurface
) : com.otaliastudios.opengl.surface.EglSurface(eglCore, eglSurface) {

    /**
     * Calls eglSwapBuffers. Use this to "publish" the current frame.
     * Returns false on failure.
     */
    @Suppress("unused")
    fun swapBuffers(): Boolean {
        // This makes no sense for offscreen surfaces
        return eglCore.swapSurfaceBuffers(eglSurface)
    }
}