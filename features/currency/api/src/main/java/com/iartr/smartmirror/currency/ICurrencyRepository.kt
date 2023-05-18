package com.iartr.smartmirror.currency

import kotlinx.coroutines.flow.Flow

interface ICurrencyRepository {
    fun getCurrencyExchangeRub(): Flow<ExchangeRates>
}