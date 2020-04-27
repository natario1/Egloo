plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    setCompileSdkVersion(rootProject.extra["compileSdkVersion"] as Int)

    defaultConfig {
        applicationId = "com.otaliastudios.zoom.demo"
        setMinSdkVersion(rootProject.extra["minSdkVersion"] as Int)
        setTargetSdkVersion(rootProject.extra["targetSdkVersion"] as Int)
        versionCode = 1
        versionName = "1.0"
    }

    // required by ExoPlayer
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("com.google.android.exoplayer:exoplayer-core:2.10.4")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.10.4")
    implementation(project(":library"))
}
