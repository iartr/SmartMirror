package com.iartr.smartmirror.weather.api

import com.iartr.smartmirror.coordinates.api.ICoordRepository
import com.iartr.smartmirror.network.retrofitApi
import com.iartr.smartmirror.weather.IWeatherRepository
import com.iartr.smartmirror.weather.WeatherNetworkDataSource
import com.iartr.smartmirror.weather.WeatherRepository

class WeatherFeatureApi {
    fun repository(coordinatesProvider: ICoordRepository): IWeatherRepository {
        return WeatherRepository(
            networkDataSource = network(),
            coordinatesProvider = coordinatesProvider
        )
    }

    private fun network(): WeatherNetworkDataSource {
        return retrofitApi("https://api.openweathermap.org/data/2.5/")
    }
}