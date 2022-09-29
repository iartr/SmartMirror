package com.iartr.smartmirror

import android.app.Application
import androidx.recyclerview.widget.ListAdapter
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.iartr.smartmirror.account.IAccountRepository
import com.iartr.smartmirror.accountsettings.AccountSettingFeatureApi
import com.iartr.smartmirror.accountsettings.di.AccountSettingsFeatureComponent
import com.iartr.smartmirror.accountsettings.di.AccountSettingsFeatureDependenciesStore
import com.iartr.smartmirror.accountsettings.di.DaggerAccountSettingsFeatureComponent
import com.iartr.smartmirror.accountsettings.di.accountSettingsFeatureComponentProvider
import com.iartr.smartmirror.camera.FacesReceiveTask
import com.iartr.smartmirror.coordinates.api.ICoordRepository
import com.iartr.smartmirror.core.utils.ActivityHelper
import com.iartr.smartmirror.core.utils.AppContextHolder
import com.iartr.smartmirror.currency.CurrencyFeatureDependencies
import com.iartr.smartmirror.currency.ICurrencyRepository
import com.iartr.smartmirror.currency.di.CurrencyFeatureComponent
import com.iartr.smartmirror.currency.di.DaggerCurrencyFeatureComponent
import com.iartr.smartmirror.di.AppComponent
import com.iartr.smartmirror.di.DaggerAppComponent
import com.iartr.smartmirror.impl.CoordFeatureDependencies
import com.iartr.smartmirror.impl.di.CoordFeatureComponent
import com.iartr.smartmirror.impl.di.DaggerCoordFeatureComponent
import com.iartr.smartmirror.mirror.MirrorFeatureDependencies
import com.iartr.smartmirror.mirror.di.DaggerMirrorFeatureComponent
import com.iartr.smartmirror.mirror.di.MirrorFeatureComponent
import com.iartr.smartmirror.mirror.di.MirrorFeatureDependenciesStore
import com.iartr.smartmirror.news.INewsRepository
import com.iartr.smartmirror.news.News
import com.iartr.smartmirror.news.NewsFeatureDependencies
import com.iartr.smartmirror.news.di.DaggerNewsFeatureComponent
import com.iartr.smartmirror.news.di.NewsFeatureComponent
import com.iartr.smartmirror.news.di.NewsFeatureDependenciesProvider
import com.iartr.smartmirror.toggles.ITogglesRepository
import com.iartr.smartmirror.weather.IWeatherRepository
import com.iartr.smartmirror.weather.WeatherFeatureDependencies
import com.iartr.smartmirror.weather.di.DaggerWeatherFeatureComponent
import com.iartr.smartmirror.weather.di.WeatherFeatureComponent
import com.iartr.smartmirror.weather.di.WeatherFeatureDependenciesStore

class SmartMirrorApplication : Application() {

    internal lateinit var appComponent: AppComponent
    internal lateinit var weatherComponent: WeatherFeatureComponent
    internal lateinit var newsComponent: NewsFeatureComponent
    internal lateinit var currencyComponent: CurrencyFeatureComponent
    internal lateinit var accountFeatureComponent: AccountSettingsFeatureComponent
    internal lateinit var coordFeatureComponent: CoordFeatureComponent
    internal lateinit var mirrorFeatureComponent: MirrorFeatureComponent

    init {
        AppContextHolder.context = this
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        MobileAds.initialize(this)
        ActivityHelper.init(this)

        loadRemoteConfig()

        initDeps()
    }

    private fun loadRemoteConfig() {
        Firebase.remoteConfig
            .apply {
                setConfigSettingsAsync(
                    remoteConfigSettings {
                        minimumFetchIntervalInSeconds = 3600
                        fetchTimeoutInSeconds = 10000
                    }
                )
            }
            .apply { setDefaultsAsync(R.xml.remote_config_defaults) }
            .fetchAndActivate()
    }

    private fun initDeps() {
        appComponent = DaggerAppComponent.factory().create(appContext = this)
        AccountSettingsFeatureDependenciesStore.deps = appComponent

        currencyComponent = DaggerCurrencyFeatureComponent.factory().create(deps = CurrencyFeatureDependencies.EMPTY)
        coordFeatureComponent = DaggerCoordFeatureComponent.factory().create(deps = CoordFeatureDependencies.EMPTY)

        WeatherFeatureDependenciesStore.deps = object : WeatherFeatureDependencies {
            override val coordinatesRepository: ICoordRepository = coordFeatureComponent.repository()
        }
        weatherComponent = DaggerWeatherFeatureComponent.factory().create(deps = WeatherFeatureDependenciesStore.deps)

        newsComponent = DaggerNewsFeatureComponent.factory().create(deps = NewsFeatureDependencies.EMPTY)
        accountFeatureComponent = DaggerAccountSettingsFeatureComponent.factory().create(deps = AccountSettingsFeatureDependenciesStore.deps)
        accountSettingsFeatureComponentProvider = lazy { accountFeatureComponent }

        MirrorFeatureDependenciesStore.deps = object : MirrorFeatureDependencies {
            override val weatherRepository: IWeatherRepository = weatherComponent.repository()
            override val newsRepository: INewsRepository = newsComponent.newsRepository()
            override val newsRecyclerAdapter: ListAdapter<News, *> = newsComponent.recyclerAdapter()
            override val currencyRepository: ICurrencyRepository = currencyComponent.repository()
            override val togglesRepository: ITogglesRepository = appComponent.togglesRepository
            override val accountRepository: IAccountRepository = appComponent.accountRepository
            override val accountSettingsApi: AccountSettingFeatureApi = accountFeatureComponent
            override val facesReceiveTask: FacesReceiveTask = appComponent.facesReceiveTask
        }
        mirrorFeatureComponent = DaggerMirrorFeatureComponent.factory().create(deps = MirrorFeatureDependenciesStore.deps)
    }
}