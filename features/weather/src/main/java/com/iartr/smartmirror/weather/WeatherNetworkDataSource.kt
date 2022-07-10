package com.iartr.smartmirror.weather

import com.iartr.smartmirror.weather.dto.Weather
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherNetworkDataSource {
    @GET("weather?appid=$APP_ID&units=metric")
    fun getCurrentWeatherByCoord(@Query("lat") lat: Double, @Query("lon") lon: Double): Single<Weather>

    private companion object {
        private const val APP_ID = "efda6346477a346c48be1683bf7dc361"
    }
}