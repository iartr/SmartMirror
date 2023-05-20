package com.iartr.smartmirror.news

import com.iartr.smartmirror.news.impl.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

internal interface NewsApi {
    @GET("everything")
    suspend fun getEverything(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY_NEWS
    ): GetEverythingResponse
}