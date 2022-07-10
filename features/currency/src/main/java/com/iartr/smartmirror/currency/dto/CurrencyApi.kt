package com.iartr.smartmirror.currency.dto

import com.google.gson.annotations.SerializedName

internal data class CurrencyResponse(
    @SerializedName("data")
    val data: CurrencyExchangeRates
)

internal data class CurrencyExchangeRates(
    @SerializedName("currency")
    val baseCurrency: CurrencyType,
    @SerializedName("rates")
    val rates: ExchangeRates
)

data class ExchangeRates(
    @SerializedName("USD")
    val usdRate: Double,
    @SerializedName("EUR")
    val eurRate: Double
)