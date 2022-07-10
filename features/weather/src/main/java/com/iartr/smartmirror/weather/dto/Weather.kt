package com.iartr.smartmirror.weather.dto

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("weather")
    val weatherDescriptions: List<WeatherDescription>,
    @SerializedName("main")
    val temperature: Temperatures
)