package com.iartr.smartmirror.data.weather

import androidx.annotation.Keep

@Keep
data class WeatherDescription(
    val main: String,
    val description: String,
    val icon: WeatherIcon
)