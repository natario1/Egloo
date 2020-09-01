package com.otaliastudios.opengl.core

import android.opengl.EGL14
import android.opengl.GLSurfaceView
import com.otaliastudios.opengl.internal.EglDisplay
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay


/**
 * Helper for [GLSurfaceView.setEGLConfigChooser], plus
 * some handy methods for configs.
 */
public object EglConfigChooser : EglNativeConfigChooser() {
    @Suppress("unused")
    @JvmField
    public val GLES2: GLSurfaceView.EGLConfigChooser = Chooser(2)

    @Suppress("unused")
    @JvmField
    public val GLES3: GLSurfaceView.EGLConfigChooser = Chooser(3)

    /**
     * Finds a suitable EGLConfig by querying [EGL14].
     */
    @Suppress("unused")
    @JvmStatic
    public fun getConfig(display: android.opengl.EGLDisplay, version: Int, recordable: Boolean): android.opengl.EGLConfig? {
        return super.getConfig(EglDisplay(display), version, recordable)?.native
    }

    private class Chooser(private val version: Int) : GLSurfaceView.EGLConfigChooser {

        // https://github.com/MasayukiSuda/ExoPlayerFilter/blob/master/epf/src/main/java/com/daasuu/epf/chooser/EConfigChooser.java
        override fun chooseConfig(egl: EGL10, display: EGLDisplay): EGLConfig {
            val configSizeArray = IntArray(1)
            val configSpec = getConfigSpec(version, true)
            if (!egl.eglChooseConfig(display, configSpec, null, 0, configSizeArray)) {
                throw IllegalArgumentException("eglChooseConfig failed")
            }
            val configSize = configSizeArray[0]
            if (configSize <= 0) throw IllegalArgumentException("No configs match configSpec")

            val configs = arrayOfNulls<EGLConfig>(configSize)
            if (!egl.eglChooseConfig(display, configSpec, configs, configSize, configSizeArray)) {
                throw IllegalArgumentException("eglChooseConfig#2 failed")
            }
            return chooseConfig(egl, display, configs.filterNotNull().toTypedArray())
                    ?: throw IllegalArgumentException("No config chosen")
        }


        // https://github.com/MasayukiSuda/ExoPlayerFilter/blob/master/epf/src/main/java/com/daasuu/epf/chooser/EConfigChooser.java
        private fun chooseConfig(egl: EGL10, display: EGLDisplay, configs: Array<EGLConfig>): EGLConfig? {
            for (config in configs) {
                val d = egl.findConfigAttrib(display, config, EGL10.EGL_DEPTH_SIZE, 0)
                val s = egl.findConfigAttrib(display, config, EGL10.EGL_STENCIL_SIZE, 0)
                if (d >= 0 && s >= 0) {
                    val r = egl.findConfigAttrib(display, config, EGL10.EGL_RED_SIZE, 0)
                    val g = egl.findConfigAttrib(display, config, EGL10.EGL_GREEN_SIZE, 0)
                    val b = egl.findConfigAttrib(display, config, EGL10.EGL_BLUE_SIZE, 0)
                    val a = egl.findConfigAttrib(display, config, EGL10.EGL_ALPHA_SIZE, 0)
                    if (r == 8 && g == 8 && b == 8 && a == 8) {
                        return config
                    }
                }
            }
            return null
        }

        private fun EGL10.findConfigAttrib(display: EGLDisplay, config: EGLConfig, attribute: Int, defaultValue: Int): Int {
            val value = IntArray(1)
            return if (eglGetConfigAttrib(display, config, attribute, value)) {
                value[0]
            } else defaultValue
        }
    }
}