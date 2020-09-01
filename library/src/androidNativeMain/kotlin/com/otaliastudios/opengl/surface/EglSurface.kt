package com.otaliastudios.opengl.surface

import com.otaliastudios.opengl.core.EglCore
import com.otaliastudios.opengl.internal.EglSurface
import platform.egl.EGLSurface

/**
 * Common base class for EGL surfaces.
 * There can be multiple base surfaces associated with a single [EglCore] object.
 */
public actual abstract class EglSurface internal actual constructor(eglCore: EglCore, eglSurface: EglSurface)
    : EglNativeSurface(eglCore, eglSurface)