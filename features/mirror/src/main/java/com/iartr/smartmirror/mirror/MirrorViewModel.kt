package com.iartr.smartmirror.mirror

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iartr.smartmirror.account.IAccountRepository
import com.iartr.smartmirror.currency.ICurrencyRepository
import com.iartr.smartmirror.ext.subscribeSuccess
import com.iartr.smartmirror.mvvm.BaseViewModel
import com.iartr.smartmirror.news.News
import com.iartr.smartmirror.news.INewsRepository
import com.iartr.smartmirror.toggles.ITogglesRepository
import com.iartr.smartmirror.toggles.TogglesSet
import com.iartr.smartmirror.core.utils.ConsumableStream
import com.iartr.smartmirror.currency.ExchangeRates
import com.iartr.smartmirror.weather.IWeatherRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class MirrorViewModel(
    private val weatherRepository: IWeatherRepository,
    private val currencyRepository: ICurrencyRepository,
    private val articlesRepository: INewsRepository,
    private val togglesRepository: ITogglesRepository,
    private val accountRepository: IAccountRepository,
    override val router: MirrorRouter
) : BaseViewModel(router) {

    private val weatherStateMutable = BehaviorSubject.createDefault<WeatherState>(WeatherState.Loading)
    val weatherState: Observable<WeatherState> = weatherStateMutable.distinctUntilChanged()

    private val currencyStateMutable = BehaviorSubject.createDefault<CurrencyState>(CurrencyState.Loading)
    val currencyState: Observable<CurrencyState> = currencyStateMutable.distinctUntilChanged()

    private val articlesStateMutable = BehaviorSubject.createDefault<ArticlesState>(ArticlesState.Loading)
    val articlesState: Observable<ArticlesState> = articlesStateMutable.distinctUntilChanged()

    private val cameraStateMutable = BehaviorSubject.createDefault<CameraState>(CameraState.Hide)
    val cameraState: Observable<CameraState> = cameraStateMutable.distinctUntilChanged()

    private val isAccountVisibleMutable = BehaviorSubject.createDefault(true)
    val isAccountVisible: Observable<Boolean> = isAccountVisibleMutable.distinctUntilChanged()

    private val googleAuthSignalMutable = ConsumableStream<Unit>()
    val googleAuthSignal: Observable<Unit> = googleAuthSignalMutable.observe()

    fun loadAll() {
        loadWeather()
        loadCurrency()
        loadArticles()
        loadCameraState()
        loadAccountState()
    }

    fun loadWeather() {
        togglesRepository.isEnabled(TogglesSet.WEATHER)
            .zipWith(weatherRepository.getCurrentWeather(), { t1, t2 -> t1 to t2 })
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
        togglesRepository.isEnabled(TogglesSet.CURRENCY)
            .zipWith(currencyRepository.getCurrencyExchangeRub(), { t1, t2 -> t1 to t2 })
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
        togglesRepository.isEnabled(TogglesSet.ARTICLES)
            .zipWith(articlesRepository.getLatest(), { t1, t2 -> t1 to t2 })
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
        togglesRepository.isEnabled(TogglesSet.CAMERA)
            .subscribeSuccess { isActive ->
                cameraStateMutable.onNext(if (isActive) CameraState.Visible else CameraState.Hide)
            }
            .addTo(disposables)
    }

    fun loadAccountState() {
        togglesRepository.isEnabled(TogglesSet.ACCOUNT)
            .subscribeSuccess { isAccountVisibleMutable.onNext(it) }
            .addTo(disposables)
    }

    fun onAccountButtonClick() {
        if (accountRepository.isLoggedIn()) {
            router.openAccount()
            return
        }
        googleAuthSignalMutable.push(Unit)
    }

    fun onGoogleAuthResult(data: Intent?) {
        accountRepository.google.auth(data)
            .subscribeSuccess { router.openAccount() }
            .addTo(disposables)
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
        data class Success(val articles: List<News>) : ArticlesState
        object Loading : ArticlesState
        object Error : ArticlesState
        object Disabled : ArticlesState
    }

    sealed interface CameraState {
        object Visible : CameraState
        object NotAvailable : CameraState
        object Hide : CameraState
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val weatherRepository: IWeatherRepository,
        private val currencyRepository: ICurrencyRepository,
        private val articlesRepository: INewsRepository,
        private val togglesRepository: ITogglesRepository,
        private val accountRepository: IAccountRepository,
        private val router: MirrorRouter
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MirrorViewModel(weatherRepository, currencyRepository, articlesRepository, togglesRepository, accountRepository, router) as T
        }

    }
}