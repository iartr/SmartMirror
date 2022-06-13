package com.iartr.smartmirror.ui.weather

data class WeekTemp(
    val date: String,
    val temp: String,
    val avgTemp:String,
    val icon: String,
    val info: String,
    val windSpeed: String,
    val humidity: String
)
