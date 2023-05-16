plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "${DefaultConfig.appId}.network"

    compileSdk = DefaultConfig.compileSdk

    defaultConfig {
        minSdk = DefaultConfig.minSdk
        targetSdk = DefaultConfig.targetSdk
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
    implementation(platform(Libs.okHttp))
    implementation(Libs.okHttpLogger)
    implementation(Libs.gson)
    implementation(Libs.retrofit)
    implementation(Libs.retrofitRxSupport)
    implementation(Libs.retrofitJsonReader)
    implementation(Libs.retrofitScalars)
    testImplementation(LibsTest.junit4)
    testImplementation(LibsTest.okhttpMockWebServer)
}