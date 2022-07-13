package com.iartr.smartmirror.currency

import io.reactivex.rxjava3.core.Single

interface ICurrencyRepository {
    fun getCurrencyExchangeRub(): Single<ExchangeRates>
}