package com.iartr.smartmirror.news

import io.reactivex.rxjava3.core.Single

interface IArticlesRepository {
    fun getLatest(): Single<List<Article>>
}