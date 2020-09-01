buildscript {
    extra["androidMinSdkVersion"] = 18
    extra["androidCompileSdkVersion"] = 29
    extra["androidTargetSdkVersion"] = 29
    extra["kotlinVersion"] = "1.4.0"

    repositories {
        google()
        mavenCentral()
        jcenter()
    }

    dependencies {
        val kotlinVersion = property("kotlinVersion") as String
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.android.tools.build:gradle:4.0.0")
        classpath("com.otaliastudios.tools:publisher:0.3.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/natario/multiplatform")
    }
}

tasks.register("clean", Delete::class) {
    delete(buildDir)
}