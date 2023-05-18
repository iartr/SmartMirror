package com.iartr.smartmirror.impl

import com.iartr.smartmirror.coordinates.api.Coordinates
import com.iartr.smartmirror.coordinates.api.ICoordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CoordRepository @Inject constructor(
    private val coordApi: CoordsNetworkDataSource
) : ICoordRepository {
    override fun loadCoord(): Flow<Coordinates> {
        return flow { emit(coordApi.getLocation()) }
    }
}