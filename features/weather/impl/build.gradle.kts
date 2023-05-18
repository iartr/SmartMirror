import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "${DefaultConfig.appId}.weather.impl"

    compileSdk = DefaultConfig.compileSdk

    defaultConfig {
        minSdk = DefaultConfig.minSdk
        targetSdk = DefaultConfig.targetSdk

        val apikey = gradleLocalProperties(rootDir).getProperty("apikey.weather")
        buildConfigField(type = "String", name = "API_KEY_WEATHER", value = "\"$apikey\"")
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
    implementation(Libs.rxJava)
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    // Только для компонента...
    implementation(Libs.lifecycleViewModel)

    implementation(Libs.dagger2)
    kapt(Libs.dagger2Compiler)

    implementation(project(":features:weather:api"))
    implementation(project(":features:coordinates:api"))

    implementation(project(":core:network"))
    implementation(project(":core:ext"))
    implementation(project(":core:utils"))

    testImplementation(LibsTest.junit4)
}