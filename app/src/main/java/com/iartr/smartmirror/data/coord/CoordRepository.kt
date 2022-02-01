package com.iartr.smartmirror.data.coord

import io.reactivex.rxjava3.core.Single

class CoordRepository(private val coordApi: CoordApi) : ICoordRepository {
    override fun loadCoord(): Single<Coordinates> {
        return coordApi.getLocation()
    }
}