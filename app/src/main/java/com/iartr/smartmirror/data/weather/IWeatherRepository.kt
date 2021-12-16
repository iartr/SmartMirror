package com.iartr.smartmirror.data

data class Weather(
    val time: Long,
    val temperature: Int,
    val `осадки`: Any
)

interface IWeatherRepository {
    fun getWeather(day: Any): List<Weather>
}