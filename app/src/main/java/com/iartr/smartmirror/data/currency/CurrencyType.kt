package com.iartr.smartmirror.data.currency

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
enum class CurrencyType {
    @SerializedName("RUB")
    RUB,
    @SerializedName("USD")
    USD,
    @SerializedName("EUR")
    EUR;
}