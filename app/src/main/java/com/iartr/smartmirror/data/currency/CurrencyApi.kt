package com.iartr.smartmirror.data.currency

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iartr.smartmirror.network.retrofitApi
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

val currencyApi: CurrencyApi = retrofitApi("https://api.coinbase.com/v2/")

@Keep
data class CurrencyResponse(
    @SerializedName("data")
    val data: CurrencyExchangeRates
)

@Keep
data class CurrencyExchangeRates(
    @SerializedName("currency")
    val baseCurrency: CurrencyType,
    @SerializedName("rates")
    val rates: ExchangeRates
)

@Keep
data class ExchangeRates(
    @SerializedName("USD")
    val usdRate: Double,
    @SerializedName("EUR")
    val eurRate: Double
)

interface CurrencyApi {
    @GET("exchange-rates")
    fun exchangeRates(@Query("currency") baseCurrency: String) : Single<CurrencyResponse>
}