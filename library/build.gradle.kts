import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.deepmedia.tools.publisher.common.GithubScm
import io.deepmedia.tools.publisher.common.License
import io.deepmedia.tools.publisher.common.Release
import io.deepmedia.tools.publisher.sonatype.Sonatype
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
        versionName = "0.6.0"
    }
    buildTypes["release"].consumerProguardFile("proguard-rules.pro")
    sourceSets["main"].java.srcDirs("src/androidJvmMain/kotlin")
    sourceSets["main"].manifest.srcFile("src/androidJvmMain/AndroidManifest.xml")
}

// Publishing

val deploySonatypeSnapshots by tasks.registering
val deploySonatypeReleases by tasks.registering
val deployLocally by tasks.registering

publisher {
    // Common parameters.
    project.group = "com.otaliastudios.opengl"
    project.description = "Simple and lightweight OpenGL ES drawing and EGL management for Android, with object-oriented components based on Google's Grafika."
    project.url = "https://github.com/natario1/Egloo"
    project.scm = GithubScm("natario1", "Egloo")
    project.addLicense(License.MIT)
    project.addDeveloper("natario1", "mat.iavarone@gmail.com")
    release.docs = Release.DOCS_AUTO
    val dir = "../prebuilt"

    // Kotlin creates MavenPublication objects with a specific name.
    // Make sure to override the weird artifact name that KMP provides for these.
    // Make also sure to not override the packaging, as some are klib, some pom, som aar...
    // Recent kotlin versions automatically add the sources jar artifact, so we must not add it.
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
        deployLocally.dependsOn("publishToDirectory${mavenPublication.capitalize()}")
        directory(mavenPublication) {
            directory = dir
            publication = mavenPublication
            project.name = artifactId
            project.artifact = artifactId
        }

        deploySonatypeReleases.dependsOn("publishToSonatype${mavenPublication.capitalize()}")
        sonatype(mavenPublication) {
            auth.user = "SONATYPE_USER"
            auth.password = "SONATYPE_PASSWORD"
            signing.key = "SIGNING_KEY"
            signing.password = "SIGNING_PASSWORD"
            publication = mavenPublication
            project.name = artifactId
            project.artifact = artifactId
        }

        // TODO can't work, version overrides the other sonatype version! Need to fix this in publisher plugin
        /* deploySonatypeSnapshots.dependsOn("publishToSonatype${mavenPublication.capitalize()}Snapshot")
        sonatype(mavenPublication + "Snapshot") {
            repository = Sonatype.OSSRH_SNAPSHOT_1
            release.version = "latest-SNAPSHOT"
            auth.user = "SONATYPE_USER"
            auth.password = "SONATYPE_PASSWORD"
            signing.key = "SIGNING_KEY"
            signing.password = "SIGNING_PASSWORD"
            publication = mavenPublication
            project.name = artifactId
            project.artifact = artifactId
        } */
    }

    // Legacy android release (:egloo)
    deployLocally.dependsOn("publishToDirectoryLegacy")
    directory("legacy") {
        directory = dir
        component = "release"
        project.name = "Egloo"
        project.artifact = "egloo"
        release.sources = Release.SOURCES_AUTO
    }

    deploySonatypeReleases.dependsOn("publishToSonatypeLegacy")
    sonatype("legacy") {
        auth.user = "SONATYPE_USER"
        auth.password = "SONATYPE_PASSWORD"
        signing.key = "SIGNING_KEY"
        signing.password = "SIGNING_PASSWORD"
        component = "release"
        project.name = "Egloo"
        project.artifact = "egloo"
        release.sources = Release.SOURCES_AUTO
    }
}

/* afterEvaluate {
    val publishing = project.publishing
    publishing.publications.filterIsInstance<MavenPublication>().forEach {
        println("Analyzing publication ${it.name}...")
        it.artifacts.forEach {
            println("   - artifact ext=${it.extension} classifier=${it.classifier}")
        }
    }
} */
