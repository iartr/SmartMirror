package com.iartr.smartmirror.toggles

import androidx.annotation.Keep

@Keep
enum class TogglesSet(val asString: String, val defaultEnabled: Boolean) {
    WEATHER("feature_weather_enabled", true),
    CURRENCY("feature_currency_enabled", true),
    ARTICLES("feature_articles_enabled", true),
    ADS("feature_ads_enabled", true),
    ACCOUNT("feature_account_enabled", true),
    CAMERA("camera_enabled", true);
}