package com.iartr.smartmirror.weather

import com.iartr.smartmirror.coordinates.api.ICoordRepository
import com.iartr.smartmirror.weather.dto.Weather
import io.reactivex.rxjava3.core.Single

internal class WeatherRepository(
    private val networkDataSource: WeatherNetworkDataSource,
    private val coordinatesProvider: ICoordRepository
) : IWeatherRepository {
    override fun getCurrentWeather(): Single<Weather> {
        return coordinatesProvider.loadCoord()
            .flatMap { getCurrentWeatherByCoord(it.lat, it.lon) }
    }

    override fun getCurrentWeatherByCoord(lat: Double, lon: Double): Single<Weather> {
        return networkDataSource.getCurrentWeatherByCoord(lat, lon)
    }
}