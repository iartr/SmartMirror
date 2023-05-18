package com.iartr.smartmirror.coordinates.api

import kotlinx.coroutines.flow.Flow

interface ICoordRepository {
    fun loadCoord(): Flow<Coordinates>
}