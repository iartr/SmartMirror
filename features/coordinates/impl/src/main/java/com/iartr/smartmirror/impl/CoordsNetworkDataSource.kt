package com.iartr.smartmirror.impl

import com.iartr.smartmirror.coordinates.api.Coordinates
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface CoordsNetworkDataSource {
    @GET("json")
    fun getLocation(): Single<Coordinates>
}