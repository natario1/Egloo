@file:Suppress("NOTHING_TO_INLINE")

package com.otaliastudios.opengl.internal

import kotlinx.cinterop.*
import platform.egl.EGLNativeWindowType
import platform.egl.EGL_DEFAULT_DISPLAY
import platform.egl.eglGetDisplay

// Could not use the native types directly because they are typealiases themselves
// and actual classes have restrictions on that. Use data class to have equals().
internal actual data class EglSurface(val native: platform.egl.EGLSurface?)
internal actual data class EglDisplay(val native: platform.egl.EGLDisplay?)
internal actual data class EglContext(val native: platform.egl.EGLContext?)
internal actual data class EglConfig(val native: platform.egl.EGLConfig)

internal actual val EGL_NO_CONTEXT = EglContext(platform.egl.EGL_NO_CONTEXT)
internal actual val EGL_NO_DISPLAY = EglDisplay(platform.egl.EGL_NO_DISPLAY)
internal actual val EGL_NO_SURFACE = EglSurface(platform.egl.EGL_NO_SURFACE)
internal actual val EGL_SUCCESS = platform.egl.EGL_SUCCESS
internal actual val EGL_NONE = platform.egl.EGL_NONE
internal actual val EGL_WIDTH = platform.egl.EGL_WIDTH
internal actual val EGL_HEIGHT = platform.egl.EGL_HEIGHT
internal actual val EGL_READ = platform.egl.EGL_READ
internal actual val EGL_DRAW = platform.egl.EGL_DRAW
internal actual val EGL_CONTEXT_CLIENT_VERSION = platform.egl.EGL_CONTEXT_CLIENT_VERSION
internal actual val EGL_OPENGL_ES2_BIT = platform.egl.EGL_OPENGL_ES2_BIT
internal actual val EGL_OPENGL_ES3_BIT_KHR = platform.egl.EGL_OPENGL_ES3_BIT_KHR
internal actual val EGL_RED_SIZE = platform.egl.EGL_RED_SIZE
internal actual val EGL_GREEN_SIZE = platform.egl.EGL_GREEN_SIZE
internal actual val EGL_BLUE_SIZE = platform.egl.EGL_BLUE_SIZE
internal actual val EGL_ALPHA_SIZE = platform.egl.EGL_ALPHA_SIZE
internal actual val EGL_SURFACE_TYPE = platform.egl.EGL_SURFACE_TYPE
internal actual val EGL_WINDOW_BIT = platform.egl.EGL_WINDOW_BIT
internal actual val EGL_PBUFFER_BIT = platform.egl.EGL_PBUFFER_BIT
internal actual val EGL_RENDERABLE_TYPE = platform.egl.EGL_RENDERABLE_TYPE

private val TRUE = platform.egl.EGL_TRUE.toUInt()

internal actual inline fun eglChooseConfig(display: EglDisplay, attributes: IntArray, configs: Array<EglConfig?>, configsSize: Int, numConfigs: IntArray) = memScoped {
    val numConfigsPointer = allocArray<IntVar>(numConfigs.size)
    val configsPointer = allocArray<platform.egl.EGLConfigVar>(configs.size)
    val result = platform.egl.eglChooseConfig(display.native, attributes.toCValues(), configsPointer, configsSize, numConfigsPointer) == TRUE
    if (result) {
        numConfigs.indices.forEach { numConfigs[it] = numConfigsPointer[it] }
        configs.indices.forEach { configs[it] = configsPointer[it]?.let { EglConfig(it) } }
    }
    result
}
internal actual inline fun eglInitialize(display: EglDisplay, major: IntArray, minor: IntArray) = memScoped {
    val majorPointer = allocArray<IntVar>(major.size)
    val minorPointer = allocArray<IntVar>(minor.size)
    val result = platform.egl.eglInitialize(display.native, majorPointer, minorPointer) == TRUE
    if (result) {
        major.indices.forEach { major[it] = majorPointer[it] }
        minor.indices.forEach { minor[it] = minorPointer[it] }
    }
    result
}
internal actual inline fun eglCreateContext(display: EglDisplay, config: EglConfig, sharedContext: EglContext, attributes: IntArray)
        = EglContext(platform.egl.eglCreateContext(display.native, config.native, sharedContext.native, attributes.toCValues()))
internal actual inline fun eglGetDefaultDisplay() = EglDisplay(eglGetDisplay(EGL_DEFAULT_DISPLAY))
internal actual inline fun eglGetCurrentContext() = EglContext(platform.egl.eglGetCurrentContext())
internal actual inline fun eglGetCurrentDisplay() = EglDisplay(platform.egl.eglGetCurrentDisplay())
internal actual inline fun eglGetCurrentSurface(which: Int) = EglSurface(platform.egl.eglGetCurrentSurface(which))
internal actual inline fun eglQuerySurface(display: EglDisplay, surface: EglSurface, attribute: Int, out: IntArray) = memScoped {
    val outPointer = allocArray<IntVar>(out.size)
    val result = platform.egl.eglQuerySurface(display.native, surface.native, attribute, outPointer) == TRUE
    if (result) out.indices.forEach { out[it] = outPointer[it] }
    result
}
@Suppress("UNCHECKED_CAST")
internal actual inline fun eglCreateWindowSurface(display: EglDisplay, config: EglConfig, surface: Any, attributes: IntArray)
        = EglSurface(platform.egl.eglCreateWindowSurface(display.native, config.native, surface as EGLNativeWindowType, attributes.toCValues()))
internal actual inline fun eglCreatePbufferSurface(display: EglDisplay, config: EglConfig, attributes: IntArray)
        = EglSurface(platform.egl.eglCreatePbufferSurface(display.native, config.native, attributes.toCValues()))
internal actual inline fun eglMakeCurrent(display: EglDisplay, draw: EglSurface, read: EglSurface, context: EglContext)
        = platform.egl.eglMakeCurrent(display.native, draw.native, read.native, context.native) == TRUE
internal actual inline fun eglSwapBuffers(display: EglDisplay, surface: EglSurface)
        = platform.egl.eglSwapBuffers(display.native, surface.native) == TRUE
private val eglPresentationTimePointer by lazy {
    val address = platform.egl.eglGetProcAddress(procname = "eglPresentationTimeANDROID")
    address?.reinterpret<CFunction<(platform.egl.EGLDisplay?, platform.egl.EGLSurface?, platform.egl.EGLnsecsANDROID) -> Boolean>>()
}
internal actual inline fun eglPresentationTime(display: EglDisplay, surface: EglSurface, nanoseconds: Long): Boolean {
    // Not easy to access! https://youtrack.jetbrains.com/issue/KT-38626 But we have a workaround.
    return eglPresentationTimePointer?.invoke(display.native, surface.native, nanoseconds) ?: false
}
internal actual inline fun eglDestroyContext(display: EglDisplay, context: EglContext)
        = platform.egl.eglDestroyContext(display.native, context.native) == TRUE
internal actual inline fun eglDestroySurface(display: EglDisplay, surface: EglSurface)
        = platform.egl.eglDestroySurface(display.native, surface.native) == TRUE
internal actual inline fun eglReleaseThread() = platform.egl.eglReleaseThread() == TRUE
internal actual inline fun eglTerminate(display: EglDisplay) = platform.egl.eglTerminate(display.native) == TRUE
internal actual inline fun eglGetError() = platform.egl.eglGetError()