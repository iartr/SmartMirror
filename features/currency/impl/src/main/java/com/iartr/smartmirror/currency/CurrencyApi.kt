package com.iartr.smartmirror.currency

import com.iartr.smartmirror.currency.dto.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface CurrencyApi {
    @GET("exchange-rates")
    suspend fun exchangeRates(@Query("currency") baseCurrency: String): CurrencyResponse
}