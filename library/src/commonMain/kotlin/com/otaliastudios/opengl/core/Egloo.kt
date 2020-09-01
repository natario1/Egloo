package com.otaliastudios.opengl.core


import com.otaliastudios.opengl.extensions.makeIdentity
import com.otaliastudios.opengl.internal.EGL_DRAW
import com.otaliastudios.opengl.internal.EGL_SUCCESS
import com.otaliastudios.opengl.internal.GL_NO_ERROR
import com.otaliastudios.opengl.internal.eglGetCurrentContext
import com.otaliastudios.opengl.internal.eglGetCurrentDisplay
import com.otaliastudios.opengl.internal.eglGetCurrentSurface
import com.otaliastudios.opengl.internal.eglGetError
import com.otaliastudios.opengl.internal.glGetError
import com.otaliastudios.opengl.internal.gluErrorString
import com.otaliastudios.opengl.internal.intToHexString
import com.otaliastudios.opengl.internal.loge
import com.otaliastudios.opengl.internal.logi
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Contains static utilities for EGL and GLES.
 */
public object Egloo {

    public const val SIZE_OF_FLOAT: Int = 4
    public const val SIZE_OF_BYTE: Int = 1
    public const val SIZE_OF_SHORT: Int = 2
    public const val SIZE_OF_INT: Int = 4

    /**
     * Identify matrix for general use.
     */
    @JvmField
    public val IDENTITY_MATRIX: FloatArray = FloatArray(16).apply {
        makeIdentity()
    }

    /**
     * Checks for GLES errors.
     */
    @JvmStatic
    public fun checkGlError(opName: String) {
        val error = glGetError().toInt()
        if (error != GL_NO_ERROR) {
            val message = "Error during $opName: glError 0x${intToHexString(error)}: ${gluErrorString(error)}"
            loge("Egloo", message)
            throw RuntimeException(message)
        }
    }

    /**
     * Checks for EGL errors.
     */
    @JvmStatic
    public fun checkEglError(opName: String) {
        val error = eglGetError()
        if (error != EGL_SUCCESS) {
            val message = "Error during $opName: EGL error 0x${intToHexString(error)}"
            loge("Egloo", message)
            throw RuntimeException(message)
        }
    }

    /**
     * Checks for program handles.
     */
    @JvmStatic
    public fun checkGlProgramLocation(location: Int, label: String) {
        if (location < 0) {
            val message = "Unable to locate $label in program"
            loge("Egloo", message)
            throw RuntimeException(message)
        }
    }

    /**
     * Writes the current display, context, and surface to the log.
     */
    @Suppress("unused")
    @JvmStatic
    public fun logCurrent(msg: String) {
        val display = eglGetCurrentDisplay()
        val context = eglGetCurrentContext()
        val surface = eglGetCurrentSurface(EGL_DRAW)
        logi("Egloo", "Current EGL ($msg): display=$display, context=$context, surface=$surface")
    }
}