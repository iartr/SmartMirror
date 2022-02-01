package com.iartr.smartmirror.data.weather

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("weather")
    val weatherDescriptions: List<WeatherDescription>,
    @SerializedName("main")
    val temperature: Temperatures
)