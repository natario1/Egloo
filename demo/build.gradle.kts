plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    setCompileSdkVersion(property("androidCompileSdkVersion") as Int)

    defaultConfig {
        applicationId = "com.otaliastudios.opengl.demo"
        setMinSdkVersion(property("androidMinSdkVersion") as Int)
        setTargetSdkVersion(property("androidTargetSdkVersion") as Int)
        versionCode = 1
        versionName = "1.0"
    }

    // required by ExoPlayer
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("com.google.android.exoplayer:exoplayer-core:2.13.2")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.13.2")
    implementation(project(":library"))

    // For testing, instead of the project dependency:
    // implementation("com.otaliastudios.opengl:egloo:0.5.1-rc1")
    // implementation("com.otaliastudios.opengl:egloo-android:0.5.1-rc1")
}
