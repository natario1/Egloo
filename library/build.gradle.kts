import com.otaliastudios.tools.publisher.PublisherExtension.License
import com.otaliastudios.tools.publisher.PublisherExtension.Release
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("maven-publisher-bintray")
}

kotlin {
    android("androidJvm")
    val nativeConfig: KotlinNativeTarget.() -> Unit = {
        val mainSourceSet = compilations["main"].defaultSourceSet.kotlin
        val testSourceSet = compilations["test"].defaultSourceSet.kotlin
        mainSourceSet.srcDir("src/androidNativeMain/kotlin")
        testSourceSet.srcDir("src/androidNativeTest/kotlin")
        if (name == "androidNativeArm32" || name == "androidNativeX86") {
            mainSourceSet.srcDir("src/androidNative32BitMain/kotlin")
        } else if (name == "androidNativeArm64" || name == "androidNativeX64") {
            mainSourceSet.srcDir("src/androidNative64BitMain/kotlin")
        }
        binaries {
            sharedLib("egloo", listOf(RELEASE))
        }
    }
    androidNativeX64(configure = nativeConfig)
    androidNativeX86(configure = nativeConfig)
    androidNativeArm32(configure = nativeConfig)
    androidNativeArm64(configure = nativeConfig)

    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.kotlin:kotlin-stdlib-common")
            }
        }
    }
}

android {
    setCompileSdkVersion(property("compileSdkVersion") as Int)

    defaultConfig {
        setMinSdkVersion(property("minSdkVersion") as Int)
        setTargetSdkVersion(property("targetSdkVersion") as Int)
        versionName = "0.4.0"
    }

    buildTypes {
        get("release").consumerProguardFile("proguard-rules.pro")
    }

    sourceSets["main"].java.srcDirs("src/androidJvmMain/kotlin")
    sourceSets["main"].manifest.srcFile("src/androidJvmMain/AndroidManifest.xml")
}

dependencies {
    val kotlinVersion = property("kotlinVersion") as String
    api("androidx.annotation:annotation:1.1.0")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
}

publisher {
    auth.user = "BINTRAY_USER"
    auth.key = "BINTRAY_KEY"
    auth.repo = "BINTRAY_REPO"
    project.group = "com.otaliastudios.opengl"
    project.artifact = "egloo"
    project.description = "Simple and lightweight OpenGL ES drawing and EGL management for Android, with object-oriented components based on Google's Grafika."
    project.url = "https://github.com/natario1/Egloo"
    project.vcsUrl = "https://github.com/natario1/Egloo.git"
    project.addLicense(License(name = "MIT", url = "http://www.opensource.org/licenses/mit-license.php"))
    release.setSources(Release.SOURCES_AUTO)
    release.setDocs(Release.DOCS_AUTO)
}