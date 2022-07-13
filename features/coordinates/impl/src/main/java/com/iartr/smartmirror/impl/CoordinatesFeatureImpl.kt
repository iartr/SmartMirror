package com.iartr.smartmirror.impl

import com.iartr.smartmirror.coordinates.api.CoordinatesFeatureApi
import com.iartr.smartmirror.coordinates.api.ICoordRepository
import com.iartr.smartmirror.network.retrofitApi

class CoordinatesFeatureImpl : CoordinatesFeatureApi {
    override fun repository(): ICoordRepository {
        return CoordRepository(
            coordApi = retrofitApi("http://ip-api.com")
        )
    }
}