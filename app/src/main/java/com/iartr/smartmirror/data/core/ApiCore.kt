package com.iartr.smartmirror.data.core

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(
        HttpLoggingInterceptor(logger = HttpLoggingInterceptor.Logger.DEFAULT)
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    )
    .build()