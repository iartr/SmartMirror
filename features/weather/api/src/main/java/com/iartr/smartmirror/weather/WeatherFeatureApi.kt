package com.iartr.smartmirror.weather

import com.iartr.smartmirror.coordinates.api.ICoordRepository

lateinit var weatherFeatureApiProvider: Lazy<WeatherFeatureApi>

interface WeatherFeatureApi {
    fun repository(coordinatesProvider: ICoordRepository): IWeatherRepository
}