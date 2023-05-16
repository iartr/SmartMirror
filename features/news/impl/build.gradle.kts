import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "${DefaultConfig.appId}.news.impl"

    compileSdk = DefaultConfig.compileSdk

    defaultConfig {
        minSdk = DefaultConfig.minSdk
        targetSdk = DefaultConfig.targetSdk

        val apikey = gradleLocalProperties(rootDir).getProperty("apikey.news")
        buildConfigField(type = "String", name = "API_KEY_NEWS", value = "\"$apikey\"")

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
    implementation(Libs.androidKtx)
    implementation(Libs.androidAppCompat)
    implementation(Libs.androidMaterial)
    implementation(Libs.retrofit)
    implementation(Libs.gson)
    implementation(Libs.rxJava)
    implementation(Libs.glide)
    kapt(Libs.glideCompiler)

    implementation(Libs.dagger2)
    kapt(Libs.dagger2Compiler)

    implementation(project(":features:news:api"))

    implementation(project(":core:network"))
    implementation(project(":core:mvvm"))
    implementation(project(":core:ext"))
    implementation(project(":core:utils"))
    implementation(project(":core:design"))

    testImplementation(LibsTest.junit4)
}