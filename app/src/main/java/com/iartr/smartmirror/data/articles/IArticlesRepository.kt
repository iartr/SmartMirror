package com.iartr.smartmirror.data.articles

import io.reactivex.rxjava3.core.Single

interface IArticlesRepository {
    fun getLatest(): Single<List<Article>>
}