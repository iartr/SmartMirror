package com.iartr.smartmirror.data.currency

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iartr.smartmirror.data.core.okHttpClient
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.coinbase.com/v2/")
    .client(okHttpClient)
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val currencyApi: CurrencyApi = retrofit.create(CurrencyApi::class.java)

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