package com.iartr.smartmirror.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.iartr.smartmirror.deps.FirebaseBasedTogglesRepository
import com.iartr.smartmirror.toggles.ITogglesRepository
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @PrefsToggles
    @Provides
    fun provideTogglesPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("preference_toggles", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideFirebaseUserDatabase(): () -> DatabaseReference {
        return { Firebase.database.reference.child("${Firebase.auth.uid}") }
    }

    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        return Firebase.remoteConfig
    }
}