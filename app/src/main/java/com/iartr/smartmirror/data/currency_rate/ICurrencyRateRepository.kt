package com.iartr.smartmirror.data.currency_rate

import com.jjoe64.graphview.series.DataPoint
import io.reactivex.rxjava3.core.Single

interface ICurrencyRateRepository {
    fun getLatestCurrencyRates(): Single<List<CurrencyRate>>
    fun getTimeseriesRates(currency: Currency): Single<List<DataPoint>>
}