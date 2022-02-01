package com.iartr.smartmirror.data.weather

import com.iartr.smartmirror.data.coord.ICoordRepository
import io.reactivex.rxjava3.core.Single

class WeatherRepository(
    private val api: WeatherApi,
    private val coordRepository: ICoordRepository
) : IWeatherRepository {
    override fun getCurrentWeather(): Single<Weather> {
        return coordRepository.loadCoord()
            .flatMap { getCurrentWeatherByCoord(it.lat, it.lon) }
    }

    override fun getCurrentWeatherByCoord(lat: Double, lon: Double): Single<Weather> {
        return api.getCurrentWeatherByCoord(lat, lon)
    }
}