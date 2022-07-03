package com.iartr.smartmirror.ui.account

import android.content.SharedPreferences
import androidx.annotation.Keep
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.iartr.smartmirror.core.utils.AppContextHolder
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class FeaturesRepository {
    // cache
    // DI
    private val fbRemoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private val fbUserDatabase: DatabaseReference
        get() = Firebase.database.reference.child("${Firebase.auth.uid}")
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
        AppContextHolder.context)

    fun isEnabled(feature: FeatureSet): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            val remoteConfigValue = fbRemoteConfig.getBoolean(feature.asString)
            val preferenceValue = preferences.getBoolean(feature.asString, feature.defaultEnabled)
            fbUserDatabase.child(feature.asString).get()
                .addOnSuccessListener {
                    val dbValue = it.getValue<Boolean>() ?: feature.defaultEnabled
                    emitter.onSuccess(remoteConfigValue && preferenceValue && dbValue)
                }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    fun setEnabled(feature: FeatureSet, isEnabled: Boolean): Completable {
        return Completable.create { emitter ->
            fbUserDatabase.child(feature.asString).setValue(isEnabled)
                .addOnSuccessListener {
                    preferences.edit { putBoolean(feature.asString, isEnabled) }
                    emitter.onComplete()
                }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    @Keep
    enum class FeatureSet(val asString: String, val defaultEnabled: Boolean) {
        WEATHER("feature_weather_enabled", true),
        CURRENCY("feature_currency_enabled", true),
        ARTICLES("feature_articles_enabled", true),
        ADS("feature_ads_enabled", true),
        ACCOUNT("feature_account_enabled", true),
        CAMERA("camera_enabled", true);
    }
}