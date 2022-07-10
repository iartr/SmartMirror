import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
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
}

dependencies {
    implementation(Libs.androidKtx)
    implementation(Libs.androidAppCompat)
    implementation(Libs.androidMaterial)
    implementation(Libs.retrofit)
    implementation(Libs.gson)
    implementation(Libs.rxJava)
    implementation(Libs.glide)
    annotationProcessor(Libs.glideCompiler)

    implementation(project(":core:network"))
    implementation(project(":core:mvvm"))
    implementation(project(":core:ext"))
    implementation(project(":core:utils"))
    implementation(project(":core:design"))

    testImplementation(LibsTest.junit4)
}