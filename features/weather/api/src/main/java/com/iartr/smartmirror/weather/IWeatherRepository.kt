package com.iartr.smartmirror.weather

import com.iartr.smartmirror.weather.dto.Weather
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    fun getCurrentWeather(): Flow<Weather>
    fun getCurrentWeatherByCoord(lat: Double, lon: Double): Flow<Weather>
}