package com.iartr.smartmirror.weather

import com.iartr.smartmirror.coordinates.api.ICoordRepository

interface WeatherFeatureDependencies {
    val coordinatesRepository: ICoordRepository
}