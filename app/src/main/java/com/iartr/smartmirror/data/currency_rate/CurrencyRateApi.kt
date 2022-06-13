package com.iartr.smartmirror.data.currency_rate

import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.apilayer.com/exchangerates_data/")
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val currencyRateApi = retrofit.create(CurrencyRateApi::class.java)

interface CurrencyRateApi {
    @GET("latest")
    @Headers("apikey: ${Constants.API_KEY}")
    fun getLatestCurrencyRates(
        @Query("base") baseCurrency: String,
        @Query("symbols") symbolCurrency: String
    ): Single<String>

    @GET("timeseries")
    @Headers("apikey: ${Constants.API_KEY}")
    fun getTimeseriesCurrencyRates(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("base") baseCurrency: String,
        @Query("symbols") symbolCurrency: String,
    ): Single<String>
}