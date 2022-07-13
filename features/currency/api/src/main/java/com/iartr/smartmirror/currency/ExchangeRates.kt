package com.iartr.smartmirror.currency

import com.google.gson.annotations.SerializedName

data class ExchangeRates(
    @SerializedName("USD")
    val usdRate: Double,
    @SerializedName("EUR")
    val eurRate: Double
)