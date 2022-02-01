package com.iartr.smartmirror.data.weather

import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.openweathermap.org/data/2.5/")
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val weatherApi: WeatherApi = retrofit.create(WeatherApi::class.java)

interface WeatherApi {
    @GET("weather?appid=$APP_ID")
    fun getCurrentWeatherByCoord(@Query("lat") lat: Double, @Query("lon") lon: Double): Single<Weather>

    private companion object {
        private const val APP_ID = "efda6346477a346c48be1683bf7dc361"
    }
}