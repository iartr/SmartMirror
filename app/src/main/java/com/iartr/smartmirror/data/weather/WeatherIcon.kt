package com.iartr.smartmirror.data.weather

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
enum class WeatherIcon {
    @SerializedName("01d")
    CLEAR_SKY,

    @SerializedName("02d")
    FEW_CLOUDS,
    @SerializedName("03d")
    FULL_CLOUDS,
    @SerializedName("04d")
    BROKEN_CLOUDS,

    @SerializedName("09d")
    SHOWER_RAIN,
    @SerializedName("10d")
    RAIN,
    @SerializedName("11d")
    THUNDERSTORM,

    @SerializedName("13d")
    SNOW,

    @SerializedName("50d")
    MIST;
}