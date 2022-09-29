package com.iartr.smartmirror.news

import androidx.annotation.Keep

@Keep
internal data class GetEverythingResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<News>
)