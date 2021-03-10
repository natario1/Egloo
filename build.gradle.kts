buildscript {
    extra["androidMinSdkVersion"] = 18
    extra["androidCompileSdkVersion"] = 30
    extra["androidTargetSdkVersion"] = 30

    repositories {
        google()
        mavenCentral()
        jcenter()
        mavenLocal()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }

    configurations.configureEach {
        resolutionStrategy {
            cacheChangingModulesFor(0, TimeUnit.SECONDS)
        }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
        classpath("com.android.tools.build:gradle:4.1.1")
        classpath("io.deepmedia.tools:publisher:latest-SNAPSHOT") {
            isChanging = true
        }
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        // maven("https://dl.bintray.com/natario/multiplatform")
    }
}

tasks.register("clean", Delete::class) {
    delete(buildDir)
}