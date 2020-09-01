import com.otaliastudios.tools.publisher.common.License
import com.otaliastudios.tools.publisher.common.Release
import com.otaliastudios.tools.publisher.bintray.BintrayPublication
import com.otaliastudios.tools.publisher.local.LocalPublication
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.otaliastudios.tools.publisher")
    id("maven-publish")
}

fun KotlinMultiplatformExtension.newSourceSet(name: String, parent: KotlinSourceSet): KotlinSourceSet {
    return sourceSets.maybeCreate(name).apply {
        dependsOn(parent)
    }
}

fun KotlinMultiplatformExtension.androidNative(name: String = "androidNative", configure: KotlinNativeTarget.() -> Unit) {
    val androidNativeMain = newSourceSet("${name}Main", sourceSets["commonMain"])
    val androidNativeTest = newSourceSet("${name}Test", sourceSets["commonTest"])
    val androidNative32BitMain = newSourceSet("${name}32BitMain", androidNativeMain)
    val androidNative32BitTest = newSourceSet("${name}32BitTest", androidNativeTest)
    val androidNative64BitMain = newSourceSet("${name}64BitMain", androidNativeMain)
    val androidNative64BitTest = newSourceSet("${name}64BitTest", androidNativeTest)
    val targets32 = listOf(androidNativeX86(), androidNativeArm32())
    val targets64 = listOf(androidNativeX64(), androidNativeArm64())
    targets32.forEach {
        newSourceSet(it.compilations["main"].defaultSourceSet.name, androidNative32BitMain)
        newSourceSet(it.compilations["test"].defaultSourceSet.name, androidNative32BitTest)
        it.configure()
    }
    targets64.forEach {
        newSourceSet(it.compilations["main"].defaultSourceSet.name, androidNative64BitMain)
        newSourceSet(it.compilations["test"].defaultSourceSet.name, androidNative64BitTest)
        it.configure()
    }
}

kotlin {
    android("androidJvm") {
        // This enables the KMP android publication.
        publishLibraryVariants("release")
    }
    androidNative {
        binaries {
            sharedLib("egloo", listOf(RELEASE))
        }
    }
    /* val nativeConfig: KotlinNativeTarget.() -> Unit = {
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
    androidNativeArm64(configure = nativeConfig) */

    sourceSets {
        getByName("androidJvmMain") {
            dependencies {
                api("androidx.annotation:annotation:1.1.0")
            }
        }
        configureEach {
            // We use unsigned types, but do not expose them.
            // https://kotlinlang.org/docs/reference/opt-in-requirements.html
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }
    }
}

// Android JVM configuration

android {
    setCompileSdkVersion(property("androidCompileSdkVersion") as Int)
    defaultConfig {
        setMinSdkVersion(property("androidMinSdkVersion") as Int)
        setTargetSdkVersion(property("androidTargetSdkVersion") as Int)
        versionName = "0.5.2"
    }
    buildTypes["release"].consumerProguardFile("proguard-rules.pro")
    sourceSets["main"].java.srcDirs("src/androidJvmMain/kotlin")
    sourceSets["main"].manifest.srcFile("src/androidJvmMain/AndroidManifest.xml")
}

// Publishing

publisher {
    project.group = "com.otaliastudios.opengl"
    project.description = "Simple and lightweight OpenGL ES drawing and EGL management for Android, with object-oriented components based on Google's Grafika."
    project.url = "https://github.com/natario1/Egloo"
    project.vcsUrl = "https://github.com/natario1/Egloo.git"
    project.addLicense(License.MIT)
    val dir = "../prebuilt"

    // Legacy android release (:egloo)
    bintray("legacy") {
        auth.user = "BINTRAY_USER"
        auth.key = "BINTRAY_KEY"
        auth.repo = "BINTRAY_REPO_LEGACY"
        component = "release"
        project.name = "Egloo"
        project.artifact = "egloo"
        release.setSources(Release.SOURCES_AUTO)
        release.setDocs(Release.DOCS_AUTO)
    }
    directory("legacy") {
        directory = dir
        component = "release"
        project.name = "Egloo"
        project.artifact = "egloo"
        release.setSources(Release.SOURCES_AUTO)
        release.setDocs(Release.DOCS_AUTO)
    }

    // Kotlin creates MavenPublication objects with a specific name.
    // Make sure to override the weird artifact name that KMP provides for these.
    // Make also sure to not override the packaging, as some are klib, some pom, som aar...
    val multiplatformPublications = mapOf(
            "androidJvmRelease" to "egloo-android",
            "androidNativeArm32" to "egloo-androidnativearm32",
            "androidNativeArm64" to "egloo-androidnativearm64",
            "androidNativeX86" to "egloo-androidnativex86",
            "androidNativeX64" to "egloo-androidnativex64",
            "kotlinMultiplatform" to "egloo-multiplatform",
            "metadata" to "egloo-metadata"
    )
    multiplatformPublications.forEach { (mavenPublication, artifactId) ->
        bintray(mavenPublication) {
            auth.user = "BINTRAY_USER"
            auth.key = "BINTRAY_KEY"
            auth.repo = "BINTRAY_REPO"
            publication = mavenPublication
            project.name = artifactId
            project.artifact = artifactId
            if (artifactId == "egloo-android") {
                release.setDocs(Release.DOCS_AUTO)
            }
        }
        directory(mavenPublication) {
            directory = dir
            publication = mavenPublication
            project.name = artifactId
            project.artifact = artifactId
        }
    }
}
