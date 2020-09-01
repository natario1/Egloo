package com.otaliastudios.opengl.surface


import android.graphics.SurfaceTexture
import android.view.Surface
import com.otaliastudios.opengl.core.EglCore


/**
 * Recordable EGL window surface.
 * It's good practice to explicitly release() the surface, preferably from a finally block.
 */
@Suppress("unused")
public actual open class EglWindowSurface : EglNativeWindowSurface {
    private var surface: Surface? = null
    private var releaseSurface = false

    /**
     * Set releaseSurface to true if you want the Surface to be released when release() is
     * called.  This is convenient, but can interfere with framework classes that expect to
     * manage the Surface themselves (e.g. if you release a SurfaceView's Surface, the
     * surfaceDestroyed() callback won't fire).
     */
    @Suppress("unused")
    @JvmOverloads
    public constructor(eglCore: EglCore, surface: Surface, releaseSurface: Boolean = false)
            : super(eglCore, eglCore.createWindowSurface(surface)) {
        this.surface = surface
        this.releaseSurface = releaseSurface
    }

    /**
     * Associates an EGL surface with the SurfaceTexture.
     */
    @Suppress("unused")
    public constructor(eglCore: EglCore, surfaceTexture: SurfaceTexture)
            : super(eglCore, eglCore.createWindowSurface(surfaceTexture))

    /**
     * Releases any resources associated with the EGL surface.
     * Does not require that the surface's EGL context be current.
     */
    override fun release() {
        super.release()
        if (releaseSurface) {
            surface?.release()
            surface = null
        }
    }
}
