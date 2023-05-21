package com.iartr.smartmirror.mirror

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.iartr.smartmirror.account.IAccountRepository
import com.iartr.smartmirror.currency.ICurrencyRepository
import com.iartr.smartmirror.mvvm.BaseViewModel
import com.iartr.smartmirror.news.News
import com.iartr.smartmirror.news.INewsRepository
import com.iartr.smartmirror.toggles.ITogglesRepository
import com.iartr.smartmirror.toggles.TogglesSet
import com.iartr.smartmirror.currency.ExchangeRates
import com.iartr.smartmirror.weather.IWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
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

    private val cameraStateMutable = MutableStateFlow<CameraState>(CameraState.Hide)
    val cameraState: StateFlow<CameraState> = cameraStateMutable.asStateFlow()

    private val isAccountVisibleMutable = MutableStateFlow(false)
    val isAccountVisible: StateFlow<Boolean> = isAccountVisibleMutable.asStateFlow()

    private val googleAuthSignalMutable = MutableSharedFlow<Unit>(replay = 0)
    val googleAuthSignal: Flow<Unit> = googleAuthSignalMutable

    fun loadAll() {
        loadWeather()
        loadCurrency()
        loadArticles()
        loadCameraState()
        loadAccountState()
    }

    fun loadWeather() {
        togglesRepository.isEnabled(TogglesSet.WEATHER)
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
        togglesRepository.isEnabled(TogglesSet.CURRENCY)
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
        togglesRepository.isEnabled(TogglesSet.ARTICLES)
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

    private fun loadCameraState() {
        togglesRepository.isEnabled(TogglesSet.CAMERA)
            .onEach { isActive -> cameraStateMutable.emit(if (isActive) CameraState.Visible else CameraState.Hide) }
            .launchIn(viewModelScope)
    }

    private fun loadAccountState() {
        togglesRepository.isEnabled(TogglesSet.ACCOUNT)
            .onEach(isAccountVisibleMutable::emit)
            .launchIn(viewModelScope)
    }

    fun onAccountButtonClick() {
        if (accountRepository.isLoggedIn()) {
            router.openAccount()
            return
        }
        viewModelScope.launch { googleAuthSignalMutable.emit(Unit) }
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