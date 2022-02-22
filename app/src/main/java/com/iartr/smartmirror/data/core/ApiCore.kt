package com.iartr.smartmirror.data.core

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

val loggingInterceptor = Interceptor {
    val response = it.proceed(it.request())

    android.util.Log.d("HTTP_CALL", """
        request: ${response.request}
        response code: ${response.code}
        response body: ${response.body}
    """.trimIndent())

    response
}
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()