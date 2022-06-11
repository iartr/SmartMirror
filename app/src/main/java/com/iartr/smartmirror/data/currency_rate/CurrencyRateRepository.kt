package com.iartr.smartmirror.data.currency_rate

import io.reactivex.rxjava3.core.Single
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDate

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

    override fun getTimeseriesRates(currency: Currency): Single<List<Double>> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(365)
        return api.getTimeseriesCurrencyRates(
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            baseCurrency = Currency.RUB.name,
            symbolCurrency = currency.name
        ).map { jsonString ->
            val jsonObject = JSONObject(jsonString).getJSONObject("rates")
            val daysBetween =
                Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays()
            (0..daysBetween).map { startDate.plusDays(it).toString() }
                .map { 1 / jsonObject.getJSONObject(it).getDouble(currency.name) }
        }
    }
}