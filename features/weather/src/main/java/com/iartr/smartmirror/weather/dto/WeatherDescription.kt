package com.iartr.smartmirror.weather.dto

data class WeatherDescription(
    val main: String,
    val description: String,
    val icon: WeatherIcon
)