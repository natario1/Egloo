---
layout: main
title: "Egloo"
---

# Egloo

Egloo is a simple and lightweight multiplatform framework for OpenGL ES drawing and EGL management
that uses object-oriented components - hence the name Egl**oo**. It can serve as a basis for
complex drawing operations, but is mostly designed for helping in making common tasks simpler,
even for people that do not have any OpenGL experience.

Approaching OpenGL from high-level languages can be hard because of the deep differences in the OpenGL API design
with respect to a typical object-oriented context. Egloo tries to take some of these difficulties away
by creating a **thin**, flexible layer of abstraction around EGL and GLES calls.

You can take a look at the demo app or see Egloo in action in some popular Android projects:

- for camera preview and real-time filters: see [CameraView](https://github.com/natario1/CameraView)
- in a zoomable Surface: see [ZoomLayout](https://github.com/natario1/ZoomLayout)
- for transcoding videos: see [Transcoder](https://github.com/natario1/Transcoder)

<p align="center">
  <img src="static/banner.png" vspace="10" width="100%">
</p>

### Features

- EGL setup and management [[docs]](docs/egl-management)
- GLSurfaceView utilities [[docs]](docs/egl-management#glsurfaceview-utilities)
- Drawables abstraction [[docs]](docs/drawables)
- Programs abstraction [[docs]](docs/programs)
- Scenes to hold view and projection matrix [[docs]](docs/scenes)

> Starting from 0.5.0, Egloo can run on native targets. We provide an implementation for Android native libraries,
but other targets like iOS can probably be added easily. See [install info](about/install).

### Get started

Get started with [install info](about/install), [quick setup](about/getting-started), or
start reading the in-depth [documentation](docs/egl-management).

### Support

If you like the project, use it with profit, and want to thank back, please consider [donating or
becoming a supporter](extra/donate).

