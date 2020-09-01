package com.otaliastudios.opengl.surface


import com.otaliastudios.opengl.core.EglCore


/**
 * A pbuffer EGL surface.
 */
@Suppress("unused")
public open class EglOffscreenSurface(eglCore: EglCore, width: Int, height: Int)
    : EglSurface(eglCore, eglCore.createOffscreenSurface(width, height)) {
    init {
        // Cache this values
        setWidth(width)
        setHeight(height)
    }
}
