package com.iartr.smartmirror.ui.currency.graph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iartr.smartmirror.data.currency_rate.Currency
import com.iartr.smartmirror.data.currency_rate.CurrencyRateTimeSeries
import com.iartr.smartmirror.data.currency_rate.ICurrencyRateRepository
import com.iartr.smartmirror.ui.base.BaseViewModel
import com.iartr.smartmirror.utils.subscribeSuccess
import com.jjoe64.graphview.series.DataPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class CurrencyGraphViewModel(
    private val currencyRateRepository: ICurrencyRateRepository
) : BaseViewModel() {

    private val currencyRatesStateMutable =
        BehaviorSubject.createDefault<CurrencyRatesState>(CurrencyRatesState.Nothing)
    val currencyRatesState: Observable<CurrencyRatesState> =
        currencyRatesStateMutable.distinctUntilChanged()

    fun getTimeseriesCurrencyRates(currency: Currency) {
        currencyRateRepository.getTimeseriesRates(currency)
            .doOnSubscribe { currencyRatesStateMutable.onNext(CurrencyRatesState.Loading) }
            .doOnError { currencyRatesStateMutable.onNext(CurrencyRatesState.Error(currency)) }
            .subscribeSuccess { currencyRatesStateMutable.onNext(CurrencyRatesState.Success(it)) }
            .addTo(disposables)
    }

    sealed interface CurrencyRatesState {
        data class Success(val dataPoints: List<DataPoint>) : CurrencyRatesState
        object Loading : CurrencyRatesState
        object Nothing : CurrencyRatesState
        data class Error(val currency: Currency) : CurrencyRatesState
    }

    class Factory(
        private val currencyRateRepository: ICurrencyRateRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CurrencyGraphViewModel(currencyRateRepository) as T
        }
    }
}