import org.gradle.api.JavaVersion

object Releases {
    val versionCode = 1
    val versionName = "1.0"
}

object CompilerOptions {
    val javaVersion = JavaVersion.VERSION_17
}

object DefaultConfig {
    val appId = "com.iartr.smartmirror"
    val minSdk = 24
    val targetSdk = 33
    val compileSdk = 33
}

object Libs {
    // https://developer.android.com/build/releases/gradle-plugin#kts
    val androidToolsPlugin = "com.android.tools.build:gradle:${Versions.ANDROID_GRADLE_PLUGIN_VERSION}"

    // D8
    val desugar = "com.android.tools:desugar_jdk_libs:${Versions.DESUGAR_VERSION}"

    //region Kotlin
    // Kotlin
    val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN_VERSION}"
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN_VERSION}"
    //endregion

    //region Android
    // https://developer.android.com/kotlin/ktx
    val androidKtx = "androidx.core:core-ktx:${Versions.ANDROID_KTX_VERSION}"
    // https://developer.android.com/jetpack/androidx/releases/appcompat
    val androidAppCompat = "androidx.appcompat:appcompat:${Versions.ANDROID_APPCOMPAT_VERSION}"
    // https://developer.android.com/jetpack/androidx/releases/activity
    val androidActivity = "androidx.activity:activity-ktx:${Versions.ANDROID_ACTIVITY_VERSION}"
    // https://developer.android.com/jetpack/androidx/releases/fragment
    val androidFragment = "androidx.fragment:fragment-ktx:${Versions.ANDROID_FRAGMENT_VERSION}"
    // https://developer.android.com/jetpack/androidx/releases/annotation
    val androidAnnotation = "androidx.annotation:annotation::${Versions.ANDROID_ANNOTATION_VERSION}"
    // // https://developer.android.com/jetpack/androidx/releases/preference
    val androidPreference = "androidx.preference:preference-ktx:${Versions.ANDROID_PREFERENCE_VERSION}"
    val androidLegacySupport = "androidx.legacy:legacy-support-v4:${Versions.ANDROID_LEGACY_SUPPORT_VERSION}"

    val androidMaterial = "com.google.android.material:material:${Versions.ANDROID_MATERIAL_VERSION}"
    //endregion

    //region Lifecycle
    // https://developer.android.com/jetpack/androidx/releases/lifecycle
    val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE_VERSION}"
    val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE_VERSION}"
    val lifecycleViewModelSaveState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.LIFECYCLE_VERSION}"
    val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE_VERSION}"
    //endregion

    //region Firebase
    // https://firebase.google.com/docs/android/learn-more
    val firebase = "com.google.firebase:firebase-bom:${Versions.FIREBASE_VERSION}"
    val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
    val firebaseDatabase = "com.google.firebase:firebase-database-ktx"
    val firebaseAuth = "com.google.firebase:firebase-auth-ktx"
    val firebaseConfig = "com.google.firebase:firebase-config-ktx"
    //endregion

    //region Play services
    // https://firebase.google.com/docs/android/android-play-services
    val playServicesPlugin = "com.google.gms:google-services:${Versions.PLAY_SERVICES_PLUGIN_VERSION}"
    val playServices = "com.google.android.gms:play-services-auth:${Versions.PLAY_SERVICES_VERSION}"
    val googleAds = "com.google.android.gms:play-services-ads:${Versions.GOOGLE_ADS_VERSION}"
    //endregion

    //region Network
    // https://square.github.io/okhttp
    val okHttp = "com.squareup.okhttp3:okhttp-bom:${Versions.OKHTTP_VERSION}"
    val okHttpLogger = "com.squareup.okhttp3:logging-interceptor"
    val okHttpMockWebServer = "com.squareup.okhttp3:mockwebserver"

    // https://github.com/square/retrofit
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT_VERSION}"
    val retrofitJsonReader = "com.squareup.retrofit2:converter-gson:${Versions.RETROFIT_VERSION}"
    val retrofitScalars = "com.squareup.retrofit2:converter-scalars:${Versions.RETROFIT_VERSION}"

    // https://github.com/google/gson
    val gson = "com.google.code.gson:gson:${Versions.GSON_VERSION}"
    //endregion

    //region Imaging (Glide)
    // https://github.com/bumptech/glide
    val glide = "com.github.bumptech.glide:glide:${Versions.GLIDE_VERSION}"
    val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.GLIDE_VERSION}"
    //endregion

    //region Camera
    val camera2 = "androidx.camera:camera-camera2:${Versions.CAMERA2_VERSION}"
    val cameraXLifecycle = "androidx.camera:camera-lifecycle:${Versions.CAMERAX_LIFECYCLE_VERSION}"
    val cameraXView = "androidx.camera:camera-view:${Versions.CAMERAX_VIEW_VERSION}"
    val googleMlFaceDetection = "com.google.mlkit:face-detection:${Versions.GOOGLE_ML_FACE_DETECTION_VERSION}"
    //endregion

    //region Dagger2
    // https://github.com/google/dagger
    val dagger2 = "com.google.dagger:dagger:${Versions.DAGGER2_VERSION}"
    val dagger2Compiler = "com.google.dagger:dagger-compiler:${Versions.DAGGER2_VERSION}"
    val javaxInject = "javax.inject:javax.inject:1"
    //endregion

    //region Coroutines
    val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES_VERSION}"
    val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES_VERSION}"
    //endregion
}

object LibsTest {
    val junit4 = "junit:junit:${Versions.JUNIT4_VERSION}"
    val androidJunit = "androidx.test.ext:junit:${Versions.ANDROID_JUNIT_VERSION}"
    val androidEspressoCore = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE_VERSION}"

    val okhttpMockWebServer = "com.squareup.okhttp3:mockwebserver"

    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}

object Versions {
    const val ANDROID_GRADLE_PLUGIN_VERSION = "8.0.0"
    const val DESUGAR_VERSION = "1.1.5"

    const val KOTLIN_VERSION = "1.8.20"

    const val ANDROID_KTX_VERSION = "1.8.0"
    const val ANDROID_APPCOMPAT_VERSION = "1.4.2"
    const val ANDROID_ACTIVITY_VERSION = "1.4.0"
    const val ANDROID_FRAGMENT_VERSION = "1.4.1"
    const val ANDROID_ANNOTATION_VERSION = "1.4.0"
    const val ANDROID_PREFERENCE_VERSION = "1.2.0"
    const val ANDROID_LEGACY_SUPPORT_VERSION = "1.0.0"
    const val ANDROID_MATERIAL_VERSION = "1.6.1"

    const val LIFECYCLE_VERSION = "2.6.1"

    const val FIREBASE_VERSION = "29.1.0"
    const val PLAY_SERVICES_PLUGIN_VERSION = "4.3.12"
    const val PLAY_SERVICES_VERSION = "20.1.0"
    const val GOOGLE_ADS_VERSION = "20.5.0"

    const val OKHTTP_VERSION = "4.10.0"
    const val RETROFIT_VERSION = "2.9.0"
    const val GSON_VERSION = "2.9.0"

    const val GLIDE_VERSION = "4.13.0"

    const val CAMERA2_VERSION = "1.0.2"
    const val CAMERAX_LIFECYCLE_VERSION = "1.0.2"
    const val CAMERAX_VIEW_VERSION = "1.0.0-alpha32"
    const val GOOGLE_ML_FACE_DETECTION_VERSION = "16.1.5"

    const val DAGGER2_VERSION = "2.46.1"

    const val JUNIT4_VERSION = "4.13.2"
    const val ANDROID_JUNIT_VERSION = "1.1.3"
    const val ESPRESSO_CORE_VERSION = "3.4.0"

    const val COROUTINES_VERSION = "1.7.1"
}