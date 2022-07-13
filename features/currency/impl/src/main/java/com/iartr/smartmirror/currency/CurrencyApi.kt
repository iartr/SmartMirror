package com.iartr.smartmirror.currency

import com.iartr.smartmirror.currency.dto.CurrencyResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

internal interface CurrencyApi {
    @GET("exchange-rates")
    fun exchangeRates(@Query("currency") baseCurrency: String) : Single<CurrencyResponse>
}