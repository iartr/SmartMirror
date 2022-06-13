package com.iartr.smartmirror.data.currency_rate

import com.jjoe64.graphview.series.DataPoint
import io.reactivex.rxjava3.core.Single
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class CurrencyRateRepository(
    private val api: CurrencyRateApi
) : ICurrencyRateRepository {
    override fun getLatestCurrencyRates(): Single<List<CurrencyRate>> {
        return api.getLatestCurrencyRates(
            baseCurrency = Currency.RUB.name,
            symbolCurrency = Currency.getCodeQuery(Currency.RUB),
        ).map { jsonString ->
            val jsonObject = JSONObject(jsonString).getJSONObject("rates")
            Currency.getCodes(Currency.RUB)
                .map { CurrencyRate(it, 1 / jsonObject.getDouble(it.name)) }
        }
    }

    override fun getTimeseriesRates(currency: Currency): Single<List<DataPoint>> {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.add(Calendar.DATE, -365)
        val startDate = calendar.time
        return api.getTimeseriesCurrencyRates(
            startDate = formatter.format(startDate),
            endDate = formatter.format(endDate),
            baseCurrency = Currency.RUB.name,
            symbolCurrency = currency.name
        ).map { jsonString ->
            val jsonObject = JSONObject(jsonString).getJSONObject("rates")
            val timeseries = arrayListOf<DataPoint>()
            (0..364).map {
                timeseries.add(
                    DataPoint(
                        calendar.time,
                        1 / jsonObject.getJSONObject(formatter.format(calendar.time)).getDouble(currency.name)
                    )
                )
                calendar.add(Calendar.DATE, 1)
            }
            timeseries
        }
    }
}