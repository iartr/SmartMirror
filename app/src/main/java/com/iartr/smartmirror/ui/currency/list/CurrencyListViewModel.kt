package com.iartr.smartmirror.ui.currency.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iartr.smartmirror.data.currency_rate.CurrencyRate
import com.iartr.smartmirror.data.currency_rate.ICurrencyRateRepository
import com.iartr.smartmirror.ui.base.BaseViewModel
import com.iartr.smartmirror.utils.subscribeSuccess
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class CurrencyListViewModel(
    private val currencyRateRepository: ICurrencyRateRepository
) : BaseViewModel() {

    private val currencyRatesStateMutable =
        BehaviorSubject.createDefault<CurrencyRatesState>(CurrencyRatesState.Nothing)
    val currencyRatesState: Observable<CurrencyRatesState> =
        currencyRatesStateMutable.distinctUntilChanged()

    fun getLatestCurrencyRates() = currencyRateRepository.getLatestCurrencyRates()
        .doOnSubscribe { currencyRatesStateMutable.onNext(CurrencyRatesState.Loading) }
        .doOnError { currencyRatesStateMutable.onNext(CurrencyRatesState.Error) }
        .subscribeSuccess { currencyRatesStateMutable.onNext(CurrencyRatesState.Success(it)) }
        .addTo(disposables)

    sealed interface CurrencyRatesState {
        data class Success(val currencyRates: List<CurrencyRate>) : CurrencyRatesState
        object Nothing : CurrencyRatesState
        object Loading : CurrencyRatesState
        object Error : CurrencyRatesState
    }

    class Factory(
        private val currencyRateRepository: ICurrencyRateRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CurrencyListViewModel(currencyRateRepository) as T
        }
    }
}