package com.iartr.smartmirror.data.coord

import com.iartr.smartmirror.network.retrofitApi
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

val coordApi = retrofitApi<CoordApi>("http://ip-api.com/")

interface CoordApi {
    @GET("json")
    fun getLocation(): Single<Coordinates>
}