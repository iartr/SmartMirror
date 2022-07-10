package com.iartr.smartmirror.weather

import com.iartr.smartmirror.weather.dto.Weather
import io.reactivex.rxjava3.core.Single

interface IWeatherRepository {
    fun getCurrentWeather(): Single<Weather>
    fun getCurrentWeatherByCoord(lat: Double, lon: Double): Single<Weather>
}