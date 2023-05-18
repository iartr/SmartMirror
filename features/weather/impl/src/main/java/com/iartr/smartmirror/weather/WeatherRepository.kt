package com.iartr.smartmirror.weather

import com.iartr.smartmirror.coordinates.api.ICoordRepository
import com.iartr.smartmirror.weather.dto.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class WeatherRepository @Inject constructor(
    private val networkDataSource: WeatherNetworkDataSource,
    private val coordinatesProvider: ICoordRepository
) : IWeatherRepository {
    override fun getCurrentWeather(): Flow<Weather> {
        return coordinatesProvider.loadCoord()
            .flatMapConcat { getCurrentWeatherByCoord(it.lat, it.lon) }
    }

    override fun getCurrentWeatherByCoord(lat: Double, lon: Double): Flow<Weather> {
        return flow { emit(networkDataSource.getCurrentWeatherByCoord(lat, lon)) }
    }
}