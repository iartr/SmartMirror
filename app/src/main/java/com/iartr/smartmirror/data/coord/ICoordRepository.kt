package com.iartr.smartmirror.data.coord

import io.reactivex.rxjava3.core.Single

interface ICoordRepository {
    fun loadCoord(): Single<Coordinates>
}