import io.deepmedia.tools.publisher.common.License
import io.deepmedia.tools.publisher.common.Release
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("com.android.library")
    id("kotlin-multiplatform")
    id("io.deepmedia.tools.publisher")
}

fun KotlinMultiplatformExtension.newSourceSet(name: String, vararg parents: KotlinSourceSet): KotlinSourceSet {
    return sourceSets.maybeCreate(name).apply {
        parents.forEach { dependsOn(it) }
    }
}

// Ideally we'd have common -> androidNative -> androidNative32/64 -> androidNativeXXX, but the
// commonizer currently only works on sets whose direct children are the final targets.
// So we need to move androidNative closer to the final targets and create two chains instead:
// 1. common -> androidNative -----------------------> androidNativeXXX
// 2.                   \------> androidNative32/64 -------/
// https://kotlinlang.org/docs/reference/mpp-share-on-platforms.html
fun KotlinMultiplatformExtension.androidNative(name: String = "androidNative", configure: KotlinNativeTarget.() -> Unit) {
    val commonMain = sourceSets["commonMain"]
    val commonTest = sourceSets["commonTest"]
    val androidNativeMain = newSourceSet("${name}Main", commonMain)
    val androidNativeTest = newSourceSet("${name}Test", commonTest)
    val androidNative32BitMain = newSourceSet("${name}32BitMain", androidNativeMain)
    val androidNative64BitMain = newSourceSet("${name}64BitMain", androidNativeMain)
    val androidNative32BitTest = newSourceSet("${name}32BitTest", androidNativeTest)
    val androidNative64BitTest = newSourceSet("${name}64BitTest", androidNativeTest)
    val targets32 = listOf(androidNativeX86(), androidNativeArm32())
    val targets64 = listOf(androidNativeX64(), androidNativeArm64())
    targets32.forEach {
        newSourceSet(it.compilations["main"].defaultSourceSetName, androidNativeMain, androidNative32BitMain)
        newSourceSet(it.compilations["test"].defaultSourceSetName, androidNativeTest, androidNative32BitTest)
        it.configure()
    }
    targets64.forEach {
        newSourceSet(it.compilations["main"].defaultSourceSetName, androidNativeMain, androidNative64BitMain)
        newSourceSet(it.compilations["test"].defaultSourceSetName, androidNativeTest, androidNative64BitTest)
        it.configure()
    }
}

kotlin {
    explicitApi()
    android("androidJvm") {
        // This enables the KMP android publication.
        publishLibraryVariants("release")
    }
    androidNative {
        binaries {
            sharedLib("egloo", listOf(RELEASE))
        }
    }
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
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalMultiplatform") // optional expectation
        }
    }
}

// Android JVM configuration

android {
    setCompileSdkVersion(property("androidCompileSdkVersion") as Int)
    defaultConfig {
        setMinSdkVersion(property("androidMinSdkVersion") as Int)
        setTargetSdkVersion(property("androidTargetSdkVersion") as Int)
        versionName = "0.5.4"
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
        release.sources = Release.SOURCES_AUTO
        release.docs = Release.DOCS_AUTO
    }
    directory("legacy") {
        directory = dir
        component = "release"
        project.name = "Egloo"
        project.artifact = "egloo"
        release.sources = Release.SOURCES_AUTO
        release.docs = Release.DOCS_AUTO
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
            "kotlinMultiplatform" to "egloo-multiplatform"
            // "metadata" to "egloo-metadata" - removed in Kotlin 1.4.20 or so
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
                release.docs = Release.DOCS_AUTO
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
