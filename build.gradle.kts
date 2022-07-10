// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Libs.androidToolsPlugin)
        classpath(Libs.kotlinGradlePlugin)
        classpath(Libs.playServicesPlugin)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
    }
}