package com.iartr.smartmirror.coordinates.api

lateinit var coordinatesFeatureApiProvider: Lazy<CoordinatesFeatureApi>

interface CoordinatesFeatureApi {
    fun repository(): ICoordRepository
}