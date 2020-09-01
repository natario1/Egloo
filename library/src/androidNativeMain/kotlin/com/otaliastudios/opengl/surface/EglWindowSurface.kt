package com.otaliastudios.opengl.surface

import com.otaliastudios.opengl.core.EglCore
import platform.egl.EGLNativeWindowType


/**
 * Recordable EGL window surface.
 * It's good practice to explicitly release() the surface, preferably from a finally block.
 */
@Suppress("unused")
public actual open class EglWindowSurface(eglCore: EglCore, nativeWindow: EGLNativeWindowType)
    : EglNativeWindowSurface(eglCore, eglCore.createWindowSurface(nativeWindow))
