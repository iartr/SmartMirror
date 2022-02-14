package com.iartr.smartmirror.data.currency

import com.google.gson.annotations.SerializedName

enum class CurrencyType {
    @SerializedName("RUB")
    RUB,
    @SerializedName("USD")
    USD,
    @SerializedName("EUR")
    EUR;
}