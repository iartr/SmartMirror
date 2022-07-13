package com.iartr.smartmirror.currency.dto

import com.google.gson.annotations.SerializedName

internal data class CurrencyResponse(
    @SerializedName("data")
    val data: CurrencyExchangeRates
)