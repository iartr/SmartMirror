plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "${DefaultConfig.appId}.coordinates.impl"

    compileSdk = DefaultConfig.compileSdk

    defaultConfig {
        minSdk = DefaultConfig.minSdk

        testInstrumentationRunner = LibsTest.testInstrumentationRunner
    }

    compileOptions {
        sourceCompatibility = CompilerOptions.javaVersion
        targetCompatibility = CompilerOptions.javaVersion
    }

    kotlinOptions {
        jvmTarget = CompilerOptions.javaVersion.toString()
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(Libs.retrofit)
    implementation(Libs.gson)
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    implementation(Libs.dagger2)
    kapt(Libs.dagger2Compiler)

    implementation(project(":features:coordinates:api"))

    implementation(project(":core:network"))
    implementation(project(":core:ext"))
    implementation(project(":core:utils"))

    testImplementation(LibsTest.junit4)
}