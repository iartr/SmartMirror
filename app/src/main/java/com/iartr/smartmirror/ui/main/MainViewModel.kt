package com.iartr.smartmirror.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iartr.smartmirror.data.currency.ExchangeRates
import com.iartr.smartmirror.data.currency.ICurrencyRepository
import com.iartr.smartmirror.data.weather.IWeatherRepository
import com.iartr.smartmirror.ui.base.BaseViewModel
import com.iartr.smartmirror.utils.subscribeSuccess
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class MainViewModel(
    private val weatherRepository: IWeatherRepository,
    private val currencyRepository: ICurrencyRepository
) : BaseViewModel() {
    private val weatherStateMutable = BehaviorSubject.createDefault<WeatherState>(WeatherState.Loading)
    val weatherState: Observable<WeatherState> = weatherStateMutable.distinctUntilChanged()

    private val currencyStateMutable = BehaviorSubject.createDefault<CurrencyState>(CurrencyState.Loading)
    val currencyState: Observable<CurrencyState> = currencyStateMutable.distinctUntilChanged()

    init {
        loadWeather()
        loadCurrency()
    }

    fun loadWeather() {
        weatherRepository.getCurrentWeather()
            .doOnSubscribe { weatherStateMutable.onNext(WeatherState.Loading) }
            .doOnError { weatherStateMutable.onNext(WeatherState.Error) }
            .subscribeSuccess {
                val temp = it.weatherDescriptions.first().icon.toString()
                weatherStateMutable.onNext(WeatherState.Success(temp))
            }
            .addTo(disposables)
    }

    fun loadCurrency() {
        currencyRepository.getCurrencyExchangeRub()
            .doOnSubscribe { currencyStateMutable.onNext(CurrencyState.Loading) }
            .doOnError { currencyStateMutable.onNext(CurrencyState.Error) }
            .subscribeSuccess {
                currencyStateMutable.onNext(CurrencyState.Success(it))
            }
            .addTo(disposables)
    }

    sealed interface WeatherState {
        data class Success(val temperature: String) : WeatherState
        object Loading : WeatherState
        object Error : WeatherState
    }

    sealed interface CurrencyState {
        data class Success(val exchangeRates: ExchangeRates) : CurrencyState
        object Loading : CurrencyState
        object Error : CurrencyState
    }

    class Factory(
        private val weatherRepository: IWeatherRepository,
        private val currencyRepository: ICurrencyRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(weatherRepository, currencyRepository) as T
        }
    }
}