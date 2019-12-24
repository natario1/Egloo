[![Build Status](https://github.com/natario1/Egloo/workflows/Build/badge.svg?event=push)](https://github.com/natario1/Egloo/actions)
[![Release](https://img.shields.io/github/release/natario1/Egloo.svg)](https://github.com/natario1/Egloo/releases)
[![Issues](https://img.shields.io/github/issues-raw/natario1/Egloo.svg)](https://github.com/natario1/Egloo/issues)

&#10240;  <!-- Hack to add whitespace -->

<p align="center">
  <img src="docs/static/banner.png" width="100%">
</p>

*Looking for a powerful camera library? Take a look at our [CameraView](https://github.com/natario1/CameraView).*

*Transcoding videos with Egloo? Take a look at our [Transcoder](https://github.com/natario1/Transcoder).*

*Need support, consulting, or have any other business-related question? Feel free to <a href="mailto:mat.iavarone@gmail.com">get in touch</a>.*

*Like the project, make profit from it, or simply want to thank back? Please consider [sponsoring me](https://github.com/sponsors/natario1)!*

# Egloo

Egloo (formerly EglCore) is a simple and lightweight framework for OpenGL ES drawing and EGL management
in Android that uses object-oriented components - hence the name Egl**oo**. It can serve as a basis for 
complex drawing operations, but is mostly designed for helping in making common tasks simpler,
even for people that do not have any OpenGL experience.

Approaching OpenGL from Android can be hard because of the deep differences in the OpenGL API design
with respect to a typical object-oriented context. Egloo tries to take some of these difficulties away
by creating a **thin**, flexible layer of abstraction around EGL and GLES calls.

You can take a look at the demo app or see Egloo in action in more popular projects:

- in a zoomable Surface in the [ZoomLayout](https://github.com/natario1/ZoomLayout) library
- for transcoding videos in the [Transcoder](https://github.com/natario1/Transcoder) library

```groovy
implementation 'com.otaliastudios.opengl:egloo:0.3.1'
```

## Features

- EGL setup and management [[docs]](https://natario1.github.io/docs/egl-management)
- GLSurfaceView utilities [[docs]](https://natario1.github.io/docs/egl-management#glsurfaceview-utilities)
- Drawables abstraction [[docs]](https://natario1.github.io/docs/drawables)
- Programs abstraction [[docs]](https://natario1.github.io/docs/programs)
- Scenes to hold view and projection matrix [[docs]](https://natario1.github.io/docs/scenes)

&#10240;  <!-- Hack to add whitespace -->

<p align="center">
  <img src="docs/static/screenshot-1.png" width="250" hspace="5"><img src="docs/static/screenshot-2.png" width="250" hspace="5"><img src="docs/static/screenshot-3.png" width="250" hspace="5">
</p>


&#10240;  <!-- Hack to add whitespace -->


## Support

If you like the project, make profit from it, or simply want to thank back, please consider 
[sponsoring me](https://github.com/sponsors/natario1) through the GitHub Sponsors program! 
You can have your company logo here, get private support hours or simply help me push this forward. 

Feel free to <a href="mailto:mat.iavarone@gmail.com">contact me</a> for support, consulting or any 
other business-related question.

## Setup

Please read the [official website](https://natario1.github.io/Egloo) for setup instructions and documentation.
You might also be interested in our [changelog](https://natario1.github.io/Egloo/about/changelog). 
Using Egloo is very simple. The function below will create a context, draw a red triangle and release:

```kotlin
// Configure an EGL context and window
val core = EglCore()
val window = EglWindowSurface(core, outputSurface)
window.makeCurrent()

// Draw
val drawable = GlTriangle() // GlDrawable: what to draw
val program = GlFlatProgram() // GlProgram: how to draw
program.setColor(Color.RED)
program.draw(drawable)

// Publish what we have drawn
// The outputSurface will receive our frame
window.swapBuffers()

// Release
program.release()
window.release()
core.release()
```
 
