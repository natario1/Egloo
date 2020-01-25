---
layout: page
title: "Textures"
description: "APIs for textures and framebuffer objects"
order: 4
disqus: 1
---

### The GlTexture object

The `GlTexture` object will generate and allocate an OpenGL texture. The texture can then be used to
read from it, render into it, attach to a framebuffer object and much more.

By default, the `GlTexture` is created with the `GLES11Ext.GL_TEXTURE_EXTERNAL_OES` texture target.
This means that it is suitable for using it as the output of a `SurfaceTexture`:

```kotlin
val texture = GlTexture()
val surfaceTexture = SurfaceTexture(texture.id)
// Anytime the surface texture is passed new data, its contents are put into our GlTexture
// For example, we can receive the stream of video frames:
videoPlayer.setOutputSurface(surfaceTexture)
videoPlayer.play()
```

However, different targets can be specified within the texture constructor. When using `GLES20.GL_TEXTURE_2D`,
you will probably want to use the constructor that accepts a `width` and `height` so that the buffer
is actually allocated.

> To render texture contents, just use a `GlTextureProgram` and pass the texture to it. 
See the [programs](programs#texture-program) page for details.

### The GlFramebuffer object

The `GlFramebuffer` object will generate an OpenGL framebuffer object.
You can attach textures to it by using `GlFramebuffer.attach()`, like so:

```kotlin
val texture = GlTexture()
val fbo = GlFramebuffer()
fbo.attach(texture, GLES20.GL_COLOR_ATTACHMENT0)
```

The attached textures will now receive the framebuffer contents.