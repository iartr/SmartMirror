import java.io.File
import kotlin.collections.listOf

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
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
}

dependencies {
    implementation(Libs.retrofit)
    implementation(Libs.gson)
    implementation(Libs.rxJava)

    implementation(project(":features:coordinates:api"))

    implementation(project(":core:network"))
    implementation(project(":core:ext"))
    implementation(project(":core:utils"))

    testImplementation(LibsTest.junit4)
}