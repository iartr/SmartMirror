package com.iartr.smartmirror.impl

import com.iartr.smartmirror.coordinates.api.Coordinates
import retrofit2.http.GET

interface CoordsNetworkDataSource {
    @GET("json")
    suspend fun getLocation(): Coordinates
}