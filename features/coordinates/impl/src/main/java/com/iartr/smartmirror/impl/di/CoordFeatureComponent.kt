package com.iartr.smartmirror.impl.di

import com.iartr.smartmirror.coordinates.api.CoordinatesFeatureApi
import com.iartr.smartmirror.coordinates.api.ICoordRepository
import com.iartr.smartmirror.core.utils.dagger.Feature
import com.iartr.smartmirror.impl.CoordFeatureDependencies
import com.iartr.smartmirror.impl.CoordRepository
import com.iartr.smartmirror.impl.CoordsNetworkDataSource
import com.iartr.smartmirror.network.retrofitApi
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(
    modules = [CoordModule::class, CoordBindsModule::class],
    dependencies = [CoordFeatureDependencies::class]
)
@Feature
interface CoordFeatureComponent : CoordinatesFeatureApi {
    override fun repository(): ICoordRepository

    @Component.Factory
    interface Factory {
        fun create(deps: CoordFeatureDependencies): CoordFeatureComponent
    }
}

@Module
class CoordModule {
    @Provides
    fun remoteDataSource(): CoordsNetworkDataSource {
        return retrofitApi("http://ip-api.com")
    }
}

@Module
interface CoordBindsModule {
    @Binds
    fun bindCoordRepository(repository: CoordRepository): ICoordRepository
}