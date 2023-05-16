import java.io.File
import kotlin.collections.listOf

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "${DefaultConfig.appId}.currency.api"

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
    implementation(Libs.gson)
    implementation(Libs.rxJava)

    implementation(project(":core:network"))

    testImplementation(LibsTest.junit4)
}