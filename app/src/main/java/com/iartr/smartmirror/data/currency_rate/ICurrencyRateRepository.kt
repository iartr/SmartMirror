package com.iartr.smartmirror.data.currency_rate

import io.reactivex.rxjava3.core.Single

interface ICurrencyRateRepository {
    fun getLatestCurrencyRates(): Single<List<CurrencyRate>>
    fun getTimeseriesRates(currency: Currency): Single<List<Double>>
}