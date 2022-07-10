package com.iartr.smartmirror.currency

import com.iartr.smartmirror.currency.dto.ExchangeRates
import io.reactivex.rxjava3.core.Single

interface ICurrencyRepository {
    fun getCurrencyExchangeRub(): Single<ExchangeRates>
}