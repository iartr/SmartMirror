package com.iartr.smartmirror.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor(logger = HttpLoggingInterceptor.Logger.DEFAULT)
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .build()
}