package com.iartr.smartmirror.ui.currency.graph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iartr.smartmirror.data.currency_rate.Currency
import com.iartr.smartmirror.data.currency_rate.ICurrencyRateRepository
import com.iartr.smartmirror.ui.base.BaseViewModel
import com.iartr.smartmirror.utils.subscribeSuccess
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class CurrencyGraphViewModel(
    private val currencyRateRepository: ICurrencyRateRepository
) : BaseViewModel() {

    private val _currencyRatesState =
        BehaviorSubject.createDefault<CurrencyRatesState>(CurrencyRatesState.Nothing)
    val currencyRatesState: Observable<CurrencyRatesState> = _currencyRatesState.distinctUntilChanged()

    fun getTimeseriesCurrencyRates(currency: Currency) {
        currencyRateRepository.getTimeseriesRates(currency)
            .doOnSubscribe { _currencyRatesState.onNext(CurrencyRatesState.Loading) }
            .doOnError { _currencyRatesState.onNext(CurrencyRatesState.Error(currency)) }
            .subscribeSuccess { _currencyRatesState.onNext(CurrencyRatesState.Success(it)) }
            .addTo(disposables)
    }

    sealed interface CurrencyRatesState {
        data class Success(val rates: List<Double>) : CurrencyRatesState
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