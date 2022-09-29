package com.iartr.smartmirror.currency.di

import com.iartr.smartmirror.core.utils.dagger.Feature
import com.iartr.smartmirror.currency.CurrencyApi
import com.iartr.smartmirror.currency.CurrencyFeatureApi
import com.iartr.smartmirror.currency.CurrencyFeatureDependencies
import com.iartr.smartmirror.currency.CurrencyRepository
import com.iartr.smartmirror.currency.ICurrencyRepository
import com.iartr.smartmirror.network.retrofitApi
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(
    modules = [CurrencyModule::class, CurrencyBindsModule::class],
    dependencies = [CurrencyFeatureDependencies::class]
)
@Feature
interface CurrencyFeatureComponent : CurrencyFeatureApi {
    override fun repository(): ICurrencyRepository

    @dagger.Component.Factory
    interface Factory {
        fun create(deps: CurrencyFeatureDependencies): CurrencyFeatureComponent
    }
}

@Module
internal class CurrencyModule {
    @Provides
    fun remoteDataSource(): CurrencyApi {
        return retrofitApi("https://api.coinbase.com/v2/")
    }
}

@Module
internal interface CurrencyBindsModule {
    @Binds
    fun bindRepository(repository: CurrencyRepository): ICurrencyRepository
}