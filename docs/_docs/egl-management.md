---
layout: page
title: "EGL Management"
description: "Create and manage the EGL context"
order: 1
disqus: 1
---

Creating and managing an EGL context and surface is mandatory to perform GLES drawing and
is very easy with Egloo. Classes that help in this start with the `Egl` prefix. 

### EGL context

The first thing to do is creating an EGL context. This can be done through the `EglCore` class:

```kotlin
val core = EglCore()
// At the end...
core.release()
```

The core object will configure a GLES 2 or GLES 3 compatible EGL context, based on the presence
of the `EglCore.FLAG_TRY_GLES3` class. When you are done, the core should always be released.

The core object can also accept a shared context in the constructor, so that the new EGL context
will share data with the old one.

After creation, `EglCore` can be used to create [EGL surfaces](#egl-surfaces).

### EGL surfaces

Each `EglCore` object can be used to create one or more `EglSurface`, which represent the output 
where our GLES rendered data will be drawn. Egloo supports two types of surfaces.

After usage, all surfaces should be released with `surface.release()`.

##### EglWindowSurface

The `EglWindowSurface` uses a `android.view.Surface` or `SurfaceTexture` as output, two objects that
can be considered system windows in Android. Anything drawn on this window will be passed to the
given `Surface` or `SurfaceTexture`, for display or processing.

```kotlin
// Create window and make it the current EGL surface
val window = EglWindowSurface(core, output)
window.makeCurrent()

// Draw something
// ...

// Publish drawn content into output
window.swapBuffers()
```

##### EglOffscreenSurface

The `EglOffscreenSurface` requires a `width` and a `height` in the constructor and corresponds to
an EGL pixel buffer surface which does not depend on any platform window.

```kotlin
// Create pbuffer and make it the current EGL surface
val pbuffer = EglOffscreenSurface(core, 100, 100)
pbuffer.makeCurrent()

// Draw something
// ...

// Offscreen surfaces are single buffered, so
// you don't need to swapBuffers() to publish
```

### GLSurfaceView utilities

When using the `android.opengl.GLSurfaceView` class, you can use two methods to control the EGL context
initialization. Egloo provides static implementations of these:

```kotlin
// For GLES2...
glSurfaceView.setEGLContextFactory(EglContextFactory.GLES2)
glSurfaceView.setEGLConfigChooser(EglContextFactory.GLES2)

// For GLES3...
glSurfaceView.setEGLContextFactory(EglContextFactory.GLES3)
glSurfaceView.setEGLConfigChooser(EglContextFactory.GLES3)
```
