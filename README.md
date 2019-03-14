# EglCore

EglCore is an Android library that makes EGL and OpenGL drawing simpler. It is not meant to be used for
complex drawing tasks, but rather to make common and simple tasks simpler, even for people that do not have
any OpenGL experience [like myself].

Most of the ideas here were taken from the invaluable [Google's Grafika](https://github.com/google/grafika) project,
but refactored to follow a more object-oriented interface and rewritten in Kotlin.

```groovy
implementation 'com.otaliastudios.opengl:egl-core:0.1.1'
```

See this in action in a zoomable and pannable Surface [in the ZoomLayout project](https://github.com/natario1/ZoomLayout).
