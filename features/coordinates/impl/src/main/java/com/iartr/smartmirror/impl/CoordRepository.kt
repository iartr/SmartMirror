package com.iartr.smartmirror.impl

import com.iartr.smartmirror.coordinates.api.Coordinates
import com.iartr.smartmirror.coordinates.api.ICoordRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CoordRepository @Inject constructor(
    private val coordApi: CoordsNetworkDataSource
) : ICoordRepository {
    override fun loadCoord(): Single<Coordinates> {
        return coordApi.getLocation()
    }
}