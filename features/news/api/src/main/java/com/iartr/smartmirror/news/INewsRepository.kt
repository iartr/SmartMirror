package com.iartr.smartmirror.news

import io.reactivex.rxjava3.core.Single

interface INewsRepository {
    fun getLatest(): Single<List<News>>
}