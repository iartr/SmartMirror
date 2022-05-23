package com.iartr.smartmirror.toggles

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class WeatherFeatureToggle(
    private val remoteConfig: FirebaseRemoteConfig,
    private val userDatabase: DatabaseReference
) : FeatureToggle {
    override fun isActive(): Single<Boolean> {
        return Single.create { emitter ->
            userDatabase.child(IS_WEATHER_ENABLED).get()
                .addOnSuccessListener { emitter.onSuccess(it.getValue<Boolean>() ?: true && remoteConfig.getBoolean(IS_WEATHER_ENABLED)) }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    override fun setActive(isActive: Boolean): Completable {
        return Completable.create { emitter ->
            userDatabase.child(IS_WEATHER_ENABLED).setValue(isActive)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    private companion object {
        private const val IS_WEATHER_ENABLED = "feature_weather_enabled"
    }
}

class CurrencyFeatureToggle(
    private val remoteConfig: FirebaseRemoteConfig,
    private val userDatabase: DatabaseReference
) : FeatureToggle {
    override fun isActive(): Single<Boolean> {
        return Single.create { emitter ->
            userDatabase.child(IS_CURRENCY_ENABLED).get()
                .addOnSuccessListener { emitter.onSuccess(it.getValue<Boolean>() ?: true && remoteConfig.getBoolean(IS_CURRENCY_ENABLED)) }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    override fun setActive(isActive: Boolean): Completable {
        return Completable.create { emitter ->
            userDatabase.child(IS_CURRENCY_ENABLED).setValue(isActive)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    private companion object {
        private const val IS_CURRENCY_ENABLED = "feature_currency_enabled"
    }
}

class ArticlesFeatureToggle(
    private val remoteConfig: FirebaseRemoteConfig,
    private val userDatabase: DatabaseReference
) : FeatureToggle {
    override fun isActive(): Single<Boolean> {
        return Single.create { emitter ->
            userDatabase.child(IS_ARTICLES_ENABLED).get()
                .addOnSuccessListener { emitter.onSuccess(it.getValue<Boolean>() ?: true && remoteConfig.getBoolean(IS_ARTICLES_ENABLED)) }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    override fun setActive(isActive: Boolean): Completable {
        return Completable.create { emitter ->
            userDatabase.child(IS_ARTICLES_ENABLED).setValue(isActive)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    private companion object {
        private const val IS_ARTICLES_ENABLED = "feature_articles_enabled"
    }
}

class AdsFeatureToggle(
    private val remoteConfig: FirebaseRemoteConfig,
    private val userDatabase: DatabaseReference
) : FeatureToggle {
    override fun isActive(): Single<Boolean> {
        return Single.create { emitter ->
            userDatabase.child(IS_ADS_ENABLED).get()
                .addOnSuccessListener { emitter.onSuccess(it.getValue<Boolean>() ?: true && remoteConfig.getBoolean(IS_ADS_ENABLED)) }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    override fun setActive(isActive: Boolean): Completable {
        return Completable.create { emitter ->
            userDatabase.child(IS_ADS_ENABLED).setValue(isActive)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    private companion object {
        private const val IS_ADS_ENABLED = "feature_ads_enabled"
    }
}

class AccountFeatureToggle(
    private val remoteConfig: FirebaseRemoteConfig,
    private val userDatabase: DatabaseReference
) : FeatureToggle {
    override fun isActive(): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            userDatabase.child(IS_ACCOUNT_ENABLED).get()
                .addOnSuccessListener {
                    val value = it.getValue<Boolean>() ?: true && remoteConfig.getBoolean(IS_ACCOUNT_ENABLED)
                    emitter.onSuccess(value)
                }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    override fun setActive(isActive: Boolean): Completable {
        return Completable.create { emitter ->
            userDatabase.child(IS_ACCOUNT_ENABLED).setValue(isActive)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    private companion object {
        private const val IS_ACCOUNT_ENABLED = "feature_account_enabled"
    }
}

class CameraFeatureToggle(
    private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig,
    private val userDatabase: DatabaseReference,
) : FeatureToggle {

    override fun isActive(): Single<Boolean> {
        return Single.create { emitter ->
            userDatabase.child(IS_CAMERA_ACTIVE).get()
                .addOnSuccessListener {
                    emitter.onSuccess(it.getValue<Boolean>() ?: true
                            && remoteConfig.getBoolean(IS_CAMERA_ACTIVE)
                            && PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_CAMERA_ACTIVE, true))
                }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    override fun setActive(isActive: Boolean): Completable {
        return Completable.create { emitter ->
            PreferenceManager.getDefaultSharedPreferences(context).edit { putBoolean(IS_CAMERA_ACTIVE, isActive) }
            userDatabase.child(IS_CAMERA_ACTIVE).setValue(isActive)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    private companion object {
        private const val IS_CAMERA_ACTIVE = "camera_enabled"
    }
}