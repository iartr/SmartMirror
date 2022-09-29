plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
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

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(Libs.androidKtx)
    implementation(Libs.androidAppCompat)
    implementation(Libs.androidMaterial)
    implementation(Libs.androidActivity)
    implementation(Libs.androidFragment)
    implementation(Libs.retrofit)
    implementation(Libs.gson)
    implementation(Libs.rxJava)
    implementation(Libs.glide)
    kapt(Libs.glideCompiler)

    implementation(Libs.dagger2)
    kapt(Libs.dagger2Compiler)

    implementation(Libs.cameraXView)
    implementation(project(":camera"))

    implementation(project(":features:news:api"))
    compileOnly(project(":features:news:impl"))
    implementation(project(":features:settings:api"))
    implementation(project(":features:currency:api"))
    implementation(project(":features:weather:api"))
    implementation(project(":features:coordinates:api"))
    implementation(project(":core:toggles-api"))
    implementation(project(":core:account-api"))

    implementation(project(":core:network"))
    implementation(project(":core:mvvm"))
    implementation(project(":core:ext"))
    implementation(project(":core:utils"))
    implementation(project(":core:design"))

    testImplementation(LibsTest.junit4)
}