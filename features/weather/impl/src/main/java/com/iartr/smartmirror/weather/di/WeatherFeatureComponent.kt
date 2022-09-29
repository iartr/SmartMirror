package com.iartr.smartmirror.weather.di

import com.iartr.smartmirror.core.utils.dagger.Feature
import com.iartr.smartmirror.network.retrofitApi
import com.iartr.smartmirror.weather.IWeatherRepository
import com.iartr.smartmirror.weather.WeatherFeatureApi
import com.iartr.smartmirror.weather.WeatherFeatureDependencies
import com.iartr.smartmirror.weather.WeatherNetworkDataSource
import com.iartr.smartmirror.weather.WeatherRepository
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlin.properties.Delegates

@Component(
    modules = [WeatherFeatureModule::class, WeatherBindsModule::class],
    dependencies = [WeatherFeatureDependencies::class]
)
@Feature
interface WeatherFeatureComponent : WeatherFeatureApi {
    override fun repository(): IWeatherRepository

    @Component.Factory
    interface Factory {
        fun create(
            deps: WeatherFeatureDependencies
        ): WeatherFeatureComponent
    }
}

@Module
internal class WeatherFeatureModule {
    @Provides
    fun remoteDataSource(): WeatherNetworkDataSource {
        return retrofitApi("https://api.openweathermap.org/data/2.5/")
    }
}

@Module
internal interface WeatherBindsModule {
    @Binds
    fun bindWeatherRepository(repository: WeatherRepository): IWeatherRepository
}

// ---

interface WeatherFeatureDependenciesProvider {
    val deps: WeatherFeatureDependencies

    companion object : WeatherFeatureDependenciesProvider by WeatherFeatureDependenciesStore
}

object WeatherFeatureDependenciesStore : WeatherFeatureDependenciesProvider {
    override var deps: WeatherFeatureDependencies by Delegates.notNull()
}

/*
* interface MirrorFeatureDependenciesProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: MirrorFeatureDependencies

    companion object : MirrorFeatureDependenciesProvider by MirrorFeatureDependenciesStore
}

object MirrorFeatureDependenciesStore : MirrorFeatureDependenciesProvider {
    override var deps: MirrorFeatureDependencies by Delegates.notNull()
}
*/