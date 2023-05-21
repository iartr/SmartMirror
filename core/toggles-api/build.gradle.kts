plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "${DefaultConfig.appId}.toggles"

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
}

dependencies {
    implementation(Libs.androidKtx)
    implementation(Libs.androidAppCompat)
    implementation(Libs.coroutinesCore)
    testImplementation(LibsTest.junit4)
}