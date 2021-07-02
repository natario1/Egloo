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

// https://kotlinlang.org/docs/reference/mpp-share-on-platforms.html
fun KotlinMultiplatformExtension.androidNative(name: String = "androidNative", configure: KotlinNativeTarget.() -> Unit) {
    val commonMain = sourceSets["commonMain"]
    val commonTest = sourceSets["commonTest"]

    val androidNativeMain = sourceSets.create("${name}Main") { dependsOn(commonMain) }
    val androidNativeTest = sourceSets.create("${name}Test") { dependsOn(commonTest) }

    val androidNative32BitMain = sourceSets.create("${name}32BitMain") { dependsOn(androidNativeMain) }
    val androidNative64BitMain = sourceSets.create("${name}64BitMain") { dependsOn(androidNativeMain) }
    val androidNative32BitTest = sourceSets.create("${name}32BitTest") { dependsOn(androidNativeTest) }
    val androidNative64BitTest = sourceSets.create("${name}64BitTest") { dependsOn(androidNativeTest) }

    val targets32 = listOf(androidNativeX86(), androidNativeArm32())
    val targets64 = listOf(androidNativeX64(), androidNativeArm64())
    targets32.forEach {
        it.compilations["main"].defaultSourceSet.dependsOn(androidNative32BitMain)
        it.compilations["test"].defaultSourceSet.dependsOn(androidNative32BitTest)
        it.configure()
    }
    targets64.forEach {
        it.compilations["main"].defaultSourceSet.dependsOn(androidNative64BitMain)
        it.compilations["test"].defaultSourceSet.dependsOn(androidNative64BitTest)
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
                api("androidx.annotation:annotation:1.2.0")
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
        versionName = "0.6.1"
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
            setPublication(mavenPublication, clone = true)
            directory = dir
            project.name = artifactId
            project.artifact = artifactId
            release.docs = Release.DOCS_AUTO
        }

        deploySonatypeReleases.dependsOn("publishToSonatype${mavenPublication.capitalize()}")
        sonatype(mavenPublication) {
            setPublication(mavenPublication, clone = true)
            auth.user = "SONATYPE_USER"
            auth.password = "SONATYPE_PASSWORD"
            signing.key = "SIGNING_KEY"
            signing.password = "SIGNING_PASSWORD"
            project.name = artifactId
            project.artifact = artifactId
            release.docs = Release.DOCS_AUTO
        }

        deploySonatypeSnapshots.dependsOn("publishToSonatype${mavenPublication.capitalize()}Snapshot")
        sonatype(mavenPublication + "Snapshot") {
            setPublication(mavenPublication, clone = true)
            repository = Sonatype.OSSRH_SNAPSHOT_1
            release.version = "latest-SNAPSHOT"
            auth.user = "SONATYPE_USER"
            auth.password = "SONATYPE_PASSWORD"
            signing.key = "SIGNING_KEY"
            signing.password = "SIGNING_PASSWORD"
            project.name = artifactId
            project.artifact = artifactId
            release.docs = Release.DOCS_AUTO
        }
    }

    // Legacy android release (:egloo)
    deployLocally.dependsOn("publishToDirectoryLegacy")
    directory("legacy") {
        directory = dir
        component = "release"
        project.name = "Egloo"
        project.artifact = "egloo"
        release.docs = Release.DOCS_AUTO
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
        release.docs = Release.DOCS_AUTO
        release.sources = Release.SOURCES_AUTO
    }
}

afterEvaluate {
    val publishing = project.publishing
    publishing.publications.filterIsInstance<MavenPublication>().forEach {
        println("Analyzing publication ${it.name}...")
        it.artifacts.forEach {
            println("   - artifact ext=${it.extension} classifier=${it.classifier}")
        }
    }
}
