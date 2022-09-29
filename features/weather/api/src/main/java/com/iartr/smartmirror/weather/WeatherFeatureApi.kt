package com.iartr.smartmirror.weather

interface WeatherFeatureApi {
    fun repository(): IWeatherRepository
}