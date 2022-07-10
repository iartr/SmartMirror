package com.iartr.smartmirror.data.weather

import com.iartr.smartmirror.network.retrofitApi
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

val weatherApi: WeatherApi = retrofitApi("https://api.openweathermap.org/data/2.5/")

interface WeatherApi {
    @GET("weather?appid=$APP_ID&units=metric")
    fun getCurrentWeatherByCoord(@Query("lat") lat: Double, @Query("lon") lon: Double): Single<Weather>

    private companion object {
        private const val APP_ID = "efda6346477a346c48be1683bf7dc361"
    }
}