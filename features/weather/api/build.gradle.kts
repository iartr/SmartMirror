import java.io.File
import kotlin.collections.listOf
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "${DefaultConfig.appId}.weather.api"

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

    implementation(project(":features:coordinates:api"))
}