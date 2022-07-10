package com.iartr.smartmirror.coordinates.api

import io.reactivex.rxjava3.core.Single

interface ICoordRepository {
    fun loadCoord(): Single<Coordinates>
}