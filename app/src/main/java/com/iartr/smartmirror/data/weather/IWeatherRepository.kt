package com.iartr.smartmirror.data.weather

import io.reactivex.rxjava3.core.Single

interface IWeatherRepository {
    fun getCurrentWeather(): Single<Weather>
    fun getCurrentWeatherByCoord(lat: Double, lon: Double): Single<Weather>
}