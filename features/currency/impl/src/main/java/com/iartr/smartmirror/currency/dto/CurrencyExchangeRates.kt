package com.iartr.smartmirror.currency.dto

import com.google.gson.annotations.SerializedName
import com.iartr.smartmirror.currency.CurrencyType
import com.iartr.smartmirror.currency.ExchangeRates

internal data class CurrencyExchangeRates(
    @SerializedName("currency")
    val baseCurrency: CurrencyType,
    @SerializedName("rates")
    val rates: ExchangeRates
)