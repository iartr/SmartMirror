package com.iartr.smartmirror.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iartr.smartmirror.data.articles.Article
import com.iartr.smartmirror.data.articles.IArticlesRepository
import com.iartr.smartmirror.data.currency.ExchangeRates
import com.iartr.smartmirror.data.currency.ICurrencyRepository
import com.iartr.smartmirror.data.weather.IWeatherRepository
import com.iartr.smartmirror.ui.base.BaseViewModel
import com.iartr.smartmirror.utils.subscribeSuccess
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class MainViewModel(
    private val weatherRepository: IWeatherRepository,
    private val currencyRepository: ICurrencyRepository,
    private val articlesRepository: IArticlesRepository
) : BaseViewModel() {
    private val weatherStateMutable = BehaviorSubject.createDefault<WeatherState>(WeatherState.Loading)
    val weatherState: Observable<WeatherState> = weatherStateMutable.distinctUntilChanged()

    private val currencyStateMutable = BehaviorSubject.createDefault<CurrencyState>(CurrencyState.Loading)
    val currencyState: Observable<CurrencyState> = currencyStateMutable.distinctUntilChanged()

    private val articlesStateMutable = BehaviorSubject.createDefault<ArticlesState>(ArticlesState.Loading)
    val articlesState: Observable<ArticlesState> = articlesStateMutable.distinctUntilChanged()

    init {
        loadWeather()
        loadCurrency()
        loadArticles()
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

    fun loadArticles() {
        articlesRepository.getLatest()
            .doOnSubscribe { articlesStateMutable.onNext(ArticlesState.Loading) }
            .doOnError { articlesStateMutable.onNext(ArticlesState.Error) }
            .subscribeSuccess { articlesStateMutable.onNext(ArticlesState.Success(it)) }
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

    sealed interface ArticlesState {
        data class Success(val articles: List<Article>) : ArticlesState
        object Loading : ArticlesState
        object Error : ArticlesState
    }

    class Factory(
        private val weatherRepository: IWeatherRepository,
        private val currencyRepository: ICurrencyRepository,
        private val articlesRepository: IArticlesRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(weatherRepository, currencyRepository, articlesRepository) as T
        }
    }
}