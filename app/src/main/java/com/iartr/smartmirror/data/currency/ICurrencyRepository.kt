package com.iartr.smartmirror.data.currency

import io.reactivex.rxjava3.core.Single

interface ICurrencyRepository {
    fun getCurrencyExchangeRub(): Single<ExchangeRates>
}