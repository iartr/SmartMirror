package com.iartr.smartmirror.weather

import com.iartr.smartmirror.coordinates.api.ICoordRepository
import com.iartr.smartmirror.network.retrofitApi

class WeatherFeatureImpl : WeatherFeatureApi {
    override fun repository(coordinatesProvider: ICoordRepository): IWeatherRepository {
        return WeatherRepository(
            networkDataSource = retrofitApi("https://api.openweathermap.org/data/2.5/"),
            coordinatesProvider = coordinatesProvider
        )
    }
}