package com.iartr.smartmirror.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iartr.smartmirror.data.weather.WeatherRepository
import com.iartr.smartmirror.ui.base.BaseViewModel
import com.iartr.smartmirror.utils.subscribeSuccess
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class MainViewModel(
    private val weatherRepository: WeatherRepository
) : BaseViewModel() {
    private val stateMutable = BehaviorSubject.createDefault<WeatherState>(WeatherState.Loading)
    val state: Observable<WeatherState> = stateMutable.distinctUntilChanged()

    init {
        getWeather()
    }

    fun getWeather() {
        weatherRepository.getCurrentWeather()
            .doOnSubscribe { stateMutable.onNext(WeatherState.Loading) }
            .doOnError { stateMutable.onNext(WeatherState.Error) }
            .subscribeSuccess {
                val temp = it.weatherDescriptions.first().icon.toString()
                stateMutable.onNext(WeatherState.Success(temp))
            }
            .addTo(disposables)
    }

    sealed interface WeatherState {
        data class Success(val temperature: String) : WeatherState
        object Loading : WeatherState
        object Error : WeatherState
    }

    class Factory(
        private val weatherRepository: WeatherRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(weatherRepository) as T
        }
    }
}