---
layout: page
title: "Getting Started"
description: "Getting started with Egloo"
category: about
date: 2018-12-20 17:48:58
order: 2
disqus: 1
---

Using Egloo is very easy even for those who do not have GLES experience at all.
The example below will create a context, draw a red triangle and release.

First, configure an EGL context and window:

```kotlin
// Configure an EGL context and window
val core = EglCore()
val window = EglWindowSurface(core, outputSurface)
window.makeCurrent()
```

Then draw our triangle:

```kotlin
val drawable = GlTriangle() // GlDrawable: what to draw
val program = GlFlatProgram() // GlProgram: how to draw
program.setColor(Color.RED)
program.draw(drawable)
```

Then publish what we have drawn. The `outputSurface` defined above will receive our frame:

```kotlin
window.swapBuffers()
```

Finally, release everything:

```kotlin
program.release()
window.release()
core.release()
```

Please keep reading the in-depth documentation for all the APIs and features we offer.