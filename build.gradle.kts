buildscript {
    extra["androidMinSdkVersion"] = 18
    extra["androidCompileSdkVersion"] = 30
    extra["androidTargetSdkVersion"] = 30

    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("../MavenPublisher/publisher/build/prebuilt")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }

    configurations.configureEach {
        resolutionStrategy {
            cacheChangingModulesFor(0, TimeUnit.SECONDS)
        }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
        classpath("com.android.tools.build:gradle:4.2.2")
        classpath("io.deepmedia.tools:publisher:0.6.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(buildDir)
}