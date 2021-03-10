---
layout: page
title: "Install"
description: "Integrate in your project"
order: 1
---

Egloo is publicly hosted on Maven Central repository, where you
can download the AAR package and other artifacts. To fetch with Gradle, make sure you add the
JCenter repository in your root projects `build.gradle` file:

```groovy
allprojects {
  repositories {
    mavenCentral()
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

### Snapshots

We deploy snapshots on each push to the main branch. If you want to use the latest, unreleased features,
you can do so (at your own risk) by adding the snapshot repository:

```groovy
allprojects {
  repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
  }
}
```

and changing the library version from `{{ site.github_version }}` to `latest-SNAPSHOT`.