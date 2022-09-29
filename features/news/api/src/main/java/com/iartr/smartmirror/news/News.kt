package com.iartr.smartmirror.news

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class News(
    @SerializedName("source") val source: Any?,
    @SerializedName("author") val author: String?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("url") val url: String,
    @SerializedName("urlToImage") val urlToImage: String,
    @SerializedName("publishedAt") val publishedAt: String,
    @SerializedName("content") val content: String
)