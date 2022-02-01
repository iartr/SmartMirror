package com.iartr.smartmirror.data.coord

import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private val retrofit = Retrofit.Builder()
    .baseUrl("http://ip-api.com/")
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val coordApi = retrofit.create(CoordApi::class.java)

interface CoordApi {
    @GET("json")
    fun getLocation(): Single<Coordinates>
}