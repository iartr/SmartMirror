package com.iartr.smartmirror.currency.dto

import com.google.gson.annotations.SerializedName

enum class CurrencyType {
    @SerializedName("RUB")
    RUB,
    @SerializedName("USD")
    USD,
    @SerializedName("EUR")
    EUR;
}