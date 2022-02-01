package com.iartr.smartmirror.data.weather

data class WeatherDescription(
    val main: String,
    val description: String,
    val icon: WeatherIcon
)