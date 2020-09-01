package com.otaliastudios.opengl.core

import com.otaliastudios.opengl.internal.EglContext

/**
 * Core EGL state (display, context, config).
 * The EGLContext must only be attached to one thread at a time.
 * This class is not thread-safe.
 *
 * @param sharedContext The context to share, or null if sharing is not desired.
 * @param flags Configuration bit flags, e.g. FLAG_RECORDABLE.
 */
public actual class EglCore constructor(
        sharedContext: platform.egl.EGLContext? = platform.egl.EGL_NO_CONTEXT,
        flags: Int = 0
) : EglNativeCore(EglContext(sharedContext), flags) {

    public override fun makeCurrent() {
        super.makeCurrent()
    }

    public override fun release() {
        super.release()
    }

    public companion object {

        /**
         * Constructor flag: surface must be recordable. This discourages EGL from using a
         * pixel format that cannot be converted efficiently to something usable by the video
         * encoder.
         */
        public const val FLAG_RECORDABLE: Int = EglNativeCore.FLAG_RECORDABLE

        /**
         * Constructor flag: ask for GLES3, fall back to GLES2 if not available. Without this
         * flag, GLES2 is used.
         */
        public const val FLAG_TRY_GLES3: Int = EglNativeCore.FLAG_TRY_GLES3
    }
}