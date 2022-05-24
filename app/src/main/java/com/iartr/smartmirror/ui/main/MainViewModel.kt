package com.iartr.smartmirror.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.iartr.smartmirror.BuildConfig
import com.iartr.smartmirror.data.articles.Article
import com.iartr.smartmirror.data.articles.IArticlesRepository
import com.iartr.smartmirror.data.currency.ExchangeRates
import com.iartr.smartmirror.data.currency.ICurrencyRepository
import com.iartr.smartmirror.data.weather.IWeatherRepository
import com.iartr.smartmirror.toggles.CameraFeatureToggle
import com.iartr.smartmirror.toggles.FeatureToggle
import com.iartr.smartmirror.ui.base.BaseViewModel
import com.iartr.smartmirror.utils.subscribeSuccess
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class MainViewModel(
    private val weatherRepository: IWeatherRepository,
    private val currencyRepository: ICurrencyRepository,
    private val articlesRepository: IArticlesRepository,
    private val cameraFeatureToggle: FeatureToggle,
    private val accountFeatureToggle: FeatureToggle,
    private val adsFeatureToggle: FeatureToggle,
    private val articlesFeatureToggle: FeatureToggle,
    private val currencyFeatureToggle: FeatureToggle,
    private val weatherFeatureToggle: FeatureToggle
) : BaseViewModel() {
    private val weatherStateMutable = BehaviorSubject.createDefault<WeatherState>(WeatherState.Loading)
    val weatherState: Observable<WeatherState> = weatherStateMutable.distinctUntilChanged()

    private val currencyStateMutable = BehaviorSubject.createDefault<CurrencyState>(CurrencyState.Loading)
    val currencyState: Observable<CurrencyState> = currencyStateMutable.distinctUntilChanged()

    private val articlesStateMutable = BehaviorSubject.createDefault<ArticlesState>(ArticlesState.Loading)
    val articlesState: Observable<ArticlesState> = articlesStateMutable.distinctUntilChanged()

    private val cameraStateMutable = BehaviorSubject.createDefault<CameraState>(CameraState.Hide)
    val cameraState: Observable<CameraState> = cameraStateMutable.distinctUntilChanged()

    val adListener: AdListener = object : AdListener() {
        override fun onAdFailedToLoad(p0: LoadAdError) {
            super.onAdFailedToLoad(p0)
            android.util.Log.e("ADS_TAG", "failed: $p0")
        }

        override fun onAdLoaded() {
            super.onAdLoaded()
            android.util.Log.e("ADS_TAG", "loaded")
        }
    }

    fun loadAll() {
        loadWeather()
        loadCurrency()
        loadArticles()
        loadCameraState()
    }

    fun loadWeather() {
        weatherFeatureToggle.isActive().zipWith(weatherRepository.getCurrentWeather(), { t1, t2 -> t1 to t2 })
            .doOnSubscribe { weatherStateMutable.onNext(WeatherState.Loading) }
            .doOnError { weatherStateMutable.onNext(WeatherState.Error) }
            .subscribeSuccess { (isActive, weather) ->
                if (!isActive) {
                    weatherStateMutable.onNext(WeatherState.Disabled)
                    return@subscribeSuccess
                }

//                val icon = it.weatherDescriptions.first().icon.toString()
                val icon = weather.weatherDescriptions.first().description
                val temp = weather.temperature.temp.toInt().toString()
                weatherStateMutable.onNext(WeatherState.Success(temp, icon))
            }
            .addTo(disposables)
    }

    fun loadCurrency() {
        currencyFeatureToggle.isActive().zipWith(currencyRepository.getCurrencyExchangeRub(), { t1, t2 -> t1 to t2 })
            .doOnSubscribe { currencyStateMutable.onNext(CurrencyState.Loading) }
            .doOnError { currencyStateMutable.onNext(CurrencyState.Error) }
            .subscribeSuccess { (isActive, exchangeRate) ->
                if (!isActive) {
                    currencyStateMutable.onNext(CurrencyState.Disabled)
                    return@subscribeSuccess
                }

                currencyStateMutable.onNext(CurrencyState.Success(exchangeRate))
            }
            .addTo(disposables)
    }

    fun loadArticles() {
        articlesFeatureToggle.isActive().zipWith(articlesRepository.getLatest(), { t1, t2 -> t1 to t2 })
            .doOnSubscribe { articlesStateMutable.onNext(ArticlesState.Loading) }
            .doOnError { articlesStateMutable.onNext(ArticlesState.Error) }
            .subscribeSuccess { (isActive, articles) ->
                if (!isActive) {
                    articlesStateMutable.onNext(ArticlesState.Disabled)
                    return@subscribeSuccess
                }

                articlesStateMutable.onNext(ArticlesState.Success(articles))
            }
            .addTo(disposables)
    }

    fun loadCameraState() {
        cameraFeatureToggle.isActive()
            .subscribeSuccess { isActive ->
                cameraStateMutable.onNext(if (isActive) CameraState.Visible else CameraState.Hide)
            }
            .addTo(disposables)
    }

    fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    fun getBannerUnitId(): String {
        return if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/6300978111"
        } else {
            "ca-app-pub-7136917781275978/7644983313"
        }
    }

    fun onAccountButtonLongClickListener() {

    }

    sealed interface WeatherState {
        data class Success(val temperature: String, val icon: String) : WeatherState
        object Loading : WeatherState
        object Error : WeatherState
        object Disabled : WeatherState
    }

    sealed interface CurrencyState {
        data class Success(val exchangeRates: ExchangeRates) : CurrencyState
        object Loading : CurrencyState
        object Error : CurrencyState
        object Disabled : CurrencyState
    }

    sealed interface ArticlesState {
        data class Success(val articles: List<Article>) : ArticlesState
        object Loading : ArticlesState
        object Error : ArticlesState
        object Disabled : ArticlesState
    }

    sealed interface CameraState {
        object Visible : CameraState
        object NotAvailable : CameraState
        object Hide : CameraState
    }

    class Factory(
        private val weatherRepository: IWeatherRepository,
        private val currencyRepository: ICurrencyRepository,
        private val articlesRepository: IArticlesRepository,
        private val cameraFeatureToggle: FeatureToggle,
        private val accountFeatureToggle: FeatureToggle,
        private val adsFeatureToggle: FeatureToggle,
        private val articlesFeatureToggle: FeatureToggle,
        private val currencyFeatureToggle: FeatureToggle,
        private val weatherFeatureToggle: FeatureToggle
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(weatherRepository, currencyRepository, articlesRepository, cameraFeatureToggle, accountFeatureToggle, adsFeatureToggle, articlesFeatureToggle, currencyFeatureToggle, weatherFeatureToggle) as T
        }
    }
}