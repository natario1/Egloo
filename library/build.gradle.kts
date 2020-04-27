import com.otaliastudios.tools.publisher.PublisherExtension.License
import com.otaliastudios.tools.publisher.PublisherExtension.Release

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publisher-bintray")
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