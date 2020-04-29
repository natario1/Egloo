@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.otaliastudios.opengl.internal

internal expect class EglSurface
internal expect class EglContext
internal expect class EglDisplay
internal expect class EglConfig

internal expect val EGL_NO_CONTEXT: EglContext
internal expect val EGL_NO_DISPLAY: EglDisplay
internal expect val EGL_NO_SURFACE: EglSurface
internal expect val EGL_SUCCESS: Int
internal expect val EGL_NONE: Int
internal expect val EGL_WIDTH: Int
internal expect val EGL_HEIGHT: Int
internal expect val EGL_DRAW: Int
internal expect val EGL_READ: Int
internal expect val EGL_CONTEXT_CLIENT_VERSION: Int
internal expect val EGL_OPENGL_ES2_BIT: Int
internal expect val EGL_OPENGL_ES3_BIT_KHR: Int
internal expect val EGL_RED_SIZE: Int
internal expect val EGL_GREEN_SIZE: Int
internal expect val EGL_BLUE_SIZE: Int
internal expect val EGL_ALPHA_SIZE: Int
internal expect val EGL_SURFACE_TYPE: Int
internal expect val EGL_WINDOW_BIT: Int
internal expect val EGL_PBUFFER_BIT: Int
internal expect val EGL_RENDERABLE_TYPE: Int

internal expect inline fun eglChooseConfig(display: EglDisplay, attributes: IntArray, configs: Array<EglConfig?>, configsSize: Int, numConfigs: IntArray): Boolean
internal expect inline fun eglInitialize(display: EglDisplay, major: IntArray, minor: IntArray): Boolean
internal expect inline fun eglCreateContext(display: EglDisplay, config: EglConfig, sharedContext: EglContext, attributes: IntArray): EglContext
internal expect inline fun eglGetDefaultDisplay(): EglDisplay
internal expect inline fun eglGetCurrentContext(): EglContext
internal expect inline fun eglGetCurrentDisplay(): EglDisplay
internal expect inline fun eglGetCurrentSurface(which: Int): EglSurface
internal expect inline fun eglQuerySurface(display: EglDisplay, surface: EglSurface, attribute: Int, out: IntArray): Boolean
internal expect inline fun eglCreateWindowSurface(display: EglDisplay, config: EglConfig, surface: Any, attributes: IntArray): EglSurface
internal expect inline fun eglCreatePbufferSurface(display: EglDisplay, config: EglConfig, attributes: IntArray): EglSurface
internal expect inline fun eglMakeCurrent(display: EglDisplay, draw: EglSurface, read: EglSurface, context: EglContext): Boolean
internal expect inline fun eglSwapBuffers(display: EglDisplay, surface: EglSurface): Boolean
internal expect inline fun eglPresentationTime(display: EglDisplay, surface: EglSurface, nanoseconds: Long): Boolean
internal expect inline fun eglDestroyContext(display: EglDisplay, context: EglContext): Boolean
internal expect inline fun eglDestroySurface(display: EglDisplay, surface: EglSurface): Boolean
internal expect inline fun eglReleaseThread(): Boolean
internal expect inline fun eglTerminate(display: EglDisplay): Boolean
internal expect inline fun eglGetError(): Int