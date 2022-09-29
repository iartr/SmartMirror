package com.iartr.smartmirror.currency

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class CurrencyRepository @Inject constructor(
    private val api: CurrencyApi
) : ICurrencyRepository {
    override fun getCurrencyExchangeRub(): Single<ExchangeRates> {
        return api.exchangeRates("RUB")
            .map { it.data.rates }
            .map {
                it.copy(
                    usdRate = 1 / it.usdRate,
                    eurRate = 1 / it.eurRate
                )
            }
    }
}