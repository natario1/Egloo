import com.otaliastudios.tools.publisher.common.License
import com.otaliastudios.tools.publisher.common.Release
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("kotlin-multiplatform")
}

fun KotlinMultiplatformExtension.newSourceSet(name: String, vararg parents: KotlinSourceSet): KotlinSourceSet {
    return sourceSets.maybeCreate(name).apply {
        parents.forEach { dependsOn(it) }
    }
}

fun KotlinMultiplatformExtension.androidNative(configure: KotlinNativeTarget.() -> Unit = {}) {
    val commonMain = sourceSets["commonMain"]
    val androidNativeMain = newSourceSet("androidNativeMain", commonMain)
    val targets = listOf(androidNativeX86(), androidNativeX64(), androidNativeArm32(), androidNativeArm64())
    targets.forEach {
        newSourceSet(it.compilations["main"].defaultSourceSet.name, androidNativeMain)
        it.configure()
    }
}

kotlin {
    androidNative()
    sourceSets["commonMain"].dependencies {
        api("com.otaliastudios.opengl:egloo-multiplatform:0.5.3") 
    }
}

/**
 * ISSUE WITH INTERMEDIATE SOURCE SETS / GRADLE METADATA
 * When the egloo dependencies comes from jcenter, everything goes well. When it is taken
 * from maven("https://dl.bintray.com/natario/multiplatform/"), the intermediate sets fail.
 *
 * The relevant tasks being executed are:
 * - :bug:transformCommonMainDependenciesMetadata
 * - :bug:compileCommonMainKotlinMetadata
 * - :bug:metadataCommonMainClasses
 * - :bug:runCommonizer
 * - :bug:transformAndroidNativeMainDependenciesMetadata
 * - :bug:compileAndroidNativeMainKotlinMetadata
 * - :bug:generateProjectStructureMetadata
 *
 * The first one seems to be the root of the issue. With jcenter, it pulls egloo-metadata-metadata-0.5.3-all,
 * while with the other repository it pulls egloo-metadata-metadata-0.5.3 (without -all). It seems that
 * in this second case the module metadata for the repo is not correctly read.
 *
 * Task :bug:transformCommonMainDependenciesMetadata
 *   INPUTS:
 *   - /Users/YYY/.gradle/caches/modules-2/files-2.1/com.otaliastudios.opengl/egloo-metadata/0.5.3/499ad10b99fc02c93f63d6ef0b815c243403ae27/egloo-metadata-metadata-0.5.3-all.jar
 *   - /Users/YYY/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib-common/1.4.0/1c752cce0ead8d504ccc88a4fed6471fd83ab0dd/kotlin-stdlib-common-1.4.0.jar
 *   OUTPUTS:
 *   - /Users/YYY/XXX/Egloo/bug/build/kotlinSourceSetMetadata/commonMain
 *
 * Task :bug:transformAndroidNativeMainDependenciesMetadata
 *   INPUTS:
 *   - /Users/YYY/.gradle/caches/modules-2/files-2.1/com.otaliastudios.opengl/egloo-metadata/0.5.3/499ad10b99fc02c93f63d6ef0b815c243403ae27/egloo-metadata-metadata-0.5.3-all.jar
 *   - /Users/YYY/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib-common/1.4.0/1c752cce0ead8d504ccc88a4fed6471fd83ab0dd/kotlin-stdlib-common-1.4.0.jar
 *   OUTPUTS:
 *   - /Users/YYY/XXX/Egloo/bug/build/kotlinSourceSetMetadata/androidNativeMain
 *
 * Task :bug:compileAndroidNativeMainKotlinMetadata
 *   INPUTS:
 *   - /Users/YYY/XXX/Egloo/bug/build/classes/kotlin/metadata/commonMain
 *   - /Users/YYY/XXX/Egloo/bug/build/kotlinSourceSetMetadata/commonMain/com.otaliastudios.opengl-egloo-metadata/com.otaliastudios.opengl-egloo-metadata-commonMain.klib
 *   - /Users/YYY/XXX/Egloo/bug/build/kotlinSourceSetMetadata/commonMain/com.otaliastudios.opengl-egloo-metadata/com.otaliastudios.opengl-egloo-metadata-androidNativeMain.klib
 *   - /Users/YYY/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib-common/1.4.0/1c752cce0ead8d504ccc88a4fed6471fd83ab0dd/kotlin-stdlib-common-1.4.0.jar
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.omxal
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.posix
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.gles3
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.gles2
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.gles
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.glesCommon
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.zlib
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.egl
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.linux
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.builtin
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.media
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.sles
 *   - /Users/YYY/.konan/kotlin-native-prebuilt-macos-1.4/klib/commonized/android_arm32-android_arm64-android_x64-android_x86-MS40LjA/common/org.jetbrains.kotlin.native.platform.android
 *   - /Users/YYY/XXX/Egloo/bug/src/androidNativeMain/kotlin/bug/Intermediate.kt
 *   OUTPUTS: 
 *   - /Users/YYY/XXX/Egloo/bug/build/classes/kotlin/metadata/androidNativeMain
 */

// Debugging tasks
tasks.configureEach {
    if (name in setOf("transformCommonMainDependenciesMetadata",
                    "transformAndroidNativeMainDependenciesMetadata",
                    "compileAndroidNativeMainKotlinMetadata")) {
        doLast {
            println("$name INPUTS: ${inputs.files.files.joinToString(separator = "\n")}")
            println("$name OUTPUTS: ${outputs.files.files.joinToString(separator = "\n")}")
        }
    }
}

// Debugging configurations
afterEvaluate {
    configurations.configureEach {
        if (name.endsWith("DependenciesMetadata") && !name.contains("Test")) {
            val attrs = attributes.keySet().map { it to attributes.getAttribute(it) }
            if (isCanBeResolved && false) {
                val files = resolve()
                println("Configuration $name! \n" +
                        " - attrs:[${attrs.joinToString { "${it.first}:${it.second}" }}]\n" +
                        " - extends:[${extendsFrom.joinToString { it.name }}]\n" +
                        " - artifacts:[${files.joinToString(prefix = "\n", separator = "\n\t\t")}]")
            }
        }
    }
}