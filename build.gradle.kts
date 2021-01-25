buildscript {
    extra["androidMinSdkVersion"] = 18
    extra["androidCompileSdkVersion"] = 30
    extra["androidTargetSdkVersion"] = 30

    repositories {
        google()
        mavenCentral()
        jcenter()
        mavenLocal()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        classpath("com.android.tools.build:gradle:4.1.1")
        classpath("io.deepmedia.tools:publisher:0.4.1")
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