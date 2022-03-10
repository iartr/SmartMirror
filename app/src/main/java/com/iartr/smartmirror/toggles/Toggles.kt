package com.iartr.smartmirror.toggles

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class WeatherFeatureToggle(
    private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig
) : FeatureToggle {
    override fun isActive(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_WEATHER_ENABLED, true)
                && remoteConfig.getBoolean(IS_WEATHER_ENABLED)
    }

    override fun setActive(isActive: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit { putBoolean(IS_WEATHER_ENABLED, isActive) }
    }

    private companion object {
        private const val IS_WEATHER_ENABLED = "feature_weather_enabled"
    }
}

class CurrencyFeatureToggle(
    private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig
) : FeatureToggle {
    override fun isActive(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_CURRENCY_ENABLED, true)
                && remoteConfig.getBoolean(IS_CURRENCY_ENABLED)
    }

    override fun setActive(isActive: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit { putBoolean(IS_CURRENCY_ENABLED, isActive) }
    }

    private companion object {
        private const val IS_CURRENCY_ENABLED = "feature_currency_enabled"
    }
}

class ArticlesFeatureToggle(
    private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig
) : FeatureToggle {
    override fun isActive(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_ARTICLES_ENABLED, true)
                && remoteConfig.getBoolean(IS_ARTICLES_ENABLED)
    }

    override fun setActive(isActive: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit { putBoolean(IS_ARTICLES_ENABLED, isActive) }
    }

    private companion object {
        private const val IS_ARTICLES_ENABLED = "feature_articles_enabled"
    }
}

class AdsFeatureToggle(
    private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig
) : FeatureToggle {
    override fun isActive(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_ADS_ENABLED, true)
                && remoteConfig.getBoolean(IS_ADS_ENABLED)
    }

    override fun setActive(isActive: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit { putBoolean(IS_ADS_ENABLED, isActive) }
    }

    private companion object {
        private const val IS_ADS_ENABLED = "feature_ads_enabled"
    }
}

class AccountFeatureToggle(
    private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig
) : FeatureToggle {
    override fun isActive(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_ACCOUNT_ENABLED, true)
                && remoteConfig.getBoolean(IS_ACCOUNT_ENABLED)
    }

    override fun setActive(isActive: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit { putBoolean(IS_ACCOUNT_ENABLED, isActive) }
    }

    private companion object {
        private const val IS_ACCOUNT_ENABLED = "feature_account_enabled"
    }
}

class CameraFeatureToggle(
    private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig
) : FeatureToggle {

    override fun isActive(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_CAMERA_ACTIVE, true)
                && remoteConfig.getBoolean(IS_CAMERA_ACTIVE)
    }

    override fun setActive(isActive: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit { putBoolean(IS_CAMERA_ACTIVE, isActive) }
    }

    private companion object {
        private const val IS_CAMERA_ACTIVE = "camera_enabled"
    }
}