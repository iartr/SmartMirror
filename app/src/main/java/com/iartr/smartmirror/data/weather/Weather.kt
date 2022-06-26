package com.iartr.smartmirror.data.weather

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Weather(
    @SerializedName("weather")
    val weatherDescriptions: List<WeatherDescription>,
    @SerializedName("main")
    val temperature: Temperatures
)