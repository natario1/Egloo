buildscript {
    extra["minSdkVersion"] = 18
    extra["compileSdkVersion"] = 29
    extra["targetSdkVersion"] = 29
    extra["kotlinVersion"] = "1.3.71"

    repositories {
        google()
        mavenCentral()
        jcenter()
    }

    dependencies {
        val kotlinVersion = property("kotlinVersion") as String
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.android.tools.build:gradle:3.6.3")
        classpath("com.otaliastudios.tools:publisher:0.3.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/natario/android")
    }
}

tasks.register("clean", Delete::class) {
    delete(buildDir)
}