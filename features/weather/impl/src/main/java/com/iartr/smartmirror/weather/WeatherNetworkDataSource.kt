package com.iartr.smartmirror.weather

import com.iartr.smartmirror.weather.dto.Weather
import com.iartr.smartmirror.weather.impl.BuildConfig
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

internal interface WeatherNetworkDataSource {
    @GET("weather?&units=metric")
    fun getCurrentWeatherByCoord(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appId: String = BuildConfig.API_KEY_WEATHER
    ): Single<Weather>
}