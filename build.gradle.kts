buildscript {
    extra["androidMinSdkVersion"] = 18
    extra["androidCompileSdkVersion"] = 29
    extra["androidTargetSdkVersion"] = 29

    repositories {
        google()
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.0")
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath("com.otaliastudios.tools:publisher:0.3.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/natario/multiplatform/")
    }
}

tasks.register("clean", Delete::class) {
    delete(buildDir)
}