package com.iartr.smartmirror.data.currency

import io.reactivex.rxjava3.core.Single

class CurrencyRepository(
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