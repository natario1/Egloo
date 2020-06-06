---
layout: page
title: "Install"
description: "Integrate in your project"
order: 1
---

Egloo is publicly hosted on [JCenter](https://bintray.com/natario/android/Egloo), where you
can download the AAR package and other artifacts. To fetch with Gradle, make sure you add the
JCenter repository in your root projects `build.gradle` file:

```groovy
allprojects {
  repositories {
    jcenter()
  }
}
```

Then simply download the latest version. For regular Android projects users:

```kotlin
implementation("com.otaliastudios.opengl:egloo-android:{{ site.github_version }}")
```

For Kotlin Multiplatform projects:

```kotlin
// Add a single dependency into your common Kotlin Multiplatform sourceset.
// This will include the correct artifact for each target that you want to support.
implementation("com.otaliastudios.opengl:egloo-multiplatform:{{ site.github_version }}")

// Or use granular dependencies:
implementation("com.otaliastudios.opengl:egloo-android:{{ site.github_version }}") // Android AAR
implementation("com.otaliastudios.opengl:egloo-androidnativex86:{{ site.github_version }}") // Android Native KLib
implementation("com.otaliastudios.opengl:egloo-androidnativex64:{{ site.github_version }}") // Android Native KLib
implementation("com.otaliastudios.opengl:egloo-androidnativearm32:{{ site.github_version }}") // Android Native KLib
implementation("com.otaliastudios.opengl:egloo-androidnativearm64:{{ site.github_version }}") // Android Native KLib

```

> The Android version works on API 18+, which is the only requirement and should be met by many projects nowadays.