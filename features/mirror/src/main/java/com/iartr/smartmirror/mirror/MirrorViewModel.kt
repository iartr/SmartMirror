package com.iartr.smartmirror.mirror

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

class MirrorViewModel(
    private val weatherRepository: IWeatherRepository,
    private val currencyRepository: ICurrencyRepository,
    private val articlesRepository: INewsRepository,
    private val togglesRepository: ITogglesRepository,
    private val accountRepository: IAccountRepository,
    override val router: MirrorRouter
) : BaseViewModel(router) {

    private val weatherStateMutable = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = weatherStateMutable.asStateFlow()

    private val currencyStateMutable = MutableStateFlow<CurrencyState>(CurrencyState.Loading)
    val currencyState: StateFlow<CurrencyState> = currencyStateMutable.asStateFlow()

    private val articlesStateMutable = MutableStateFlow<ArticlesState>(ArticlesState.Loading)
    val articlesState: StateFlow<ArticlesState> = articlesStateMutable.asStateFlow()

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
        togglesRepository.isEnabled2(TogglesSet.WEATHER)
            .zip(weatherRepository.getCurrentWeather(), { t1, t2 -> t1 to t2 })
            .catch { weatherStateMutable.emit(WeatherState.Error) }
            .onStart { weatherStateMutable.emit(WeatherState.Loading) }
            .onEach { (isActive, weather) ->
                if (!isActive) {
                    weatherStateMutable.emit(WeatherState.Disabled)
                    return@onEach
                }

//                val icon = it.weatherDescriptions.first().icon.toString()
                val icon = weather.weatherDescriptions.first().description
                val temp = weather.temperature.temp.toInt().toString()
                weatherStateMutable.emit(WeatherState.Success(temp, icon))
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun loadCurrency() {
        togglesRepository.isEnabled2(TogglesSet.CURRENCY)
            .zip(currencyRepository.getCurrencyExchangeRub(), { t1, t2 -> t1 to t2 })
            .catch { currencyStateMutable.emit(CurrencyState.Error) }
            .onStart { currencyStateMutable.emit(CurrencyState.Error) }
            .onEach { (isActive, exchangeRate) ->
                if (!isActive) {
                    currencyStateMutable.emit(CurrencyState.Disabled)
                    return@onEach
                }

                currencyStateMutable.emit(CurrencyState.Success(exchangeRate))
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun loadArticles() {
        togglesRepository.isEnabled2(TogglesSet.ARTICLES)
            .zip(articlesRepository.getLatest(), { t1, t2 -> t1 to t2 })
            .catch { articlesStateMutable.emit(ArticlesState.Error) }
            .onStart { articlesStateMutable.emit(ArticlesState.Loading) }
            .onEach { (isActive, articles) ->
                if (!isActive) {
                    articlesStateMutable.emit(ArticlesState.Disabled)
                    return@onEach
                }

                articlesStateMutable.emit(ArticlesState.Success(articles))
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
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
            .flowOn(Dispatchers.IO)
            .onEach { router.openAccount() }
            .launchIn(viewModelScope)
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