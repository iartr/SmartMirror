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
    implementation(Libs.androidKtx)
    implementation(Libs.androidAppCompat)
    implementation(Libs.androidActivity)
    implementation(Libs.androidFragment)
    implementation(Libs.androidMaterial)
    implementation(Libs.lifecycleRuntime)
    implementation(Libs.lifecycleViewModel)
    implementation(Libs.rxJava)
    implementation(Libs.glide)
    annotationProcessor(Libs.glideCompiler)

    implementation(project(":core:mvvm"))
    implementation(project(":core:ext"))
    implementation(project(":core:utils"))
    implementation(project(":core:design"))
    implementation(project(":core:toggles-api"))
    implementation(project(":core:account-api"))

    testImplementation(LibsTest.junit4)
}