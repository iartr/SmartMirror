import java.io.File
import kotlin.collections.listOf

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "${DefaultConfig.appId}.coordinates.api"

    compileSdk = DefaultConfig.compileSdk

    defaultConfig {
        minSdk = DefaultConfig.minSdk
        targetSdk = DefaultConfig.targetSdk

        testInstrumentationRunner = LibsTest.testInstrumentationRunner
    }

    compileOptions {
        sourceCompatibility = CompilerOptions.javaVersion
        targetCompatibility = CompilerOptions.javaVersion
    }

    kotlinOptions {
        jvmTarget = CompilerOptions.javaVersion.toString()
    }

    buildTypes {
        release {
            setProguardFiles(
                listOf(
                    File("proguard-rules.pro"),
                    getDefaultProguardFile("proguard-android-optimize.txt")
                )
            )
        }
    }
}

dependencies {
    implementation(Libs.retrofit)
    implementation(Libs.gson)
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    implementation(project(":core:network"))
    implementation(project(":core:ext"))
    implementation(project(":core:utils"))

    testImplementation(LibsTest.junit4)
}