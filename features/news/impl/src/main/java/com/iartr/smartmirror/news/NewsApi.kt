package com.iartr.smartmirror.news

import com.iartr.smartmirror.news.impl.BuildConfig
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

internal interface NewsApi {
    @GET("everything")
    fun getEverything(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY_NEWS
    ): Single<GetEverythingResponse>

    @GET("everything")
    fun getEverything2(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY_NEWS
    ): Unit
}