package com.otaliastudios.opengl.core


import android.opengl.EGL14
import android.opengl.GLES20
import android.opengl.GLU
import android.util.Log
import com.otaliastudios.opengl.extensions.makeIdentity

/**
 * Contains static utilities for EGL and GLES.
 */
object Egloo {

    const val SIZE_OF_FLOAT = 4

    /**
     * Identify matrix for general use.
     */
    @JvmStatic
    val IDENTITY_MATRIX = FloatArray(16).apply {
        makeIdentity()
    }

    /**
     * Checks for GLES errors.
     */
    @JvmStatic
    fun checkGlError(opName: String) {
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            val message = "Error during $opName: glError 0x${Integer.toHexString(error)}: ${GLU.gluErrorString(error)}"
            Log.e("Egloo", message)
            throw RuntimeException(message)
        }
    }

    /**
     * Checks for EGL errors.
     */
    @JvmStatic
    fun checkEglError(opName: String) {
        val error = EGL14.eglGetError()
        if (error != EGL14.EGL_SUCCESS) {
            val message = "Error during $opName: EGL error 0x${Integer.toHexString(error)}"
            Log.e("Egloo", message)
            throw RuntimeException(message)
        }
    }

    /**
     * Checks for program handles.
     */
    @JvmStatic
    fun checkGlProgramLocation(location: Int, label: String) {
        if (location < 0) {
            val message = "Unable to locate $label in program"
            Log.e("Egloo", message)
            throw RuntimeException(message)
        }
    }

    /**
     * Writes the current display, context, and surface to the log.
     */
    @Suppress("unused")
    @JvmStatic
    fun logCurrent(msg: String) {
        val display = EGL14.eglGetCurrentDisplay()
        val context = EGL14.eglGetCurrentContext()
        val surface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW)
        Log.i("Egloo", "Current EGL ($msg): display=$display, context=$context, surface=$surface")
    }
}