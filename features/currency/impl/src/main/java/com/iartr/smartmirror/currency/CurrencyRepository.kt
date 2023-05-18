package com.iartr.smartmirror.currency

import com.iartr.smartmirror.currency.dto.CurrencyResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class CurrencyRepository @Inject constructor(
    private val api: CurrencyApi
) : ICurrencyRepository {
    override fun getCurrencyExchangeRub(): Flow<ExchangeRates> {
        return flow<CurrencyResponse> { api.exchangeRates("RUB") }
            .map { it.data.rates }
            .map { it.copy(usdRate = 1 / it.usdRate, eurRate = 1 / it.eurRate) }
    }
}