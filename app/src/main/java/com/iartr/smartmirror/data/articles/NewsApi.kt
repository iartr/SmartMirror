package com.iartr.smartmirror.data.articles

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
    .baseUrl("https://newsapi.org/v2/")
    .client(okHttpClient)
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val newsApi: NewsApi = retrofit.create(NewsApi::class.java)

@Keep
data class GetEverythingResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

@Keep
data class Article(
    @SerializedName("source") val source: Any?,
    @SerializedName("author") val author: String?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("url") val url: String,
    @SerializedName("urlToImage") val urlToImage: String,
    @SerializedName("publishedAt") val publishedAt: String,
    @SerializedName("content") val content: String
)

interface NewsApi {
    @GET("everything?apiKey=$API_KEY")
    fun getEverything(
        @Query("q") query: String
    ): Single<GetEverythingResponse>

    private companion object {
        private const val API_KEY = "c066466eaff24646a1e7061a6e23474e"
    }
}