package com.iartr.smartmirror.news

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class NewsRepository @Inject constructor(
    private val api: NewsApi
): INewsRepository {

    override fun getLatest(): Single<List<News>> {
        return api.getEverything(query = "Mobile development")
            .map { it.articles.take(2) }
    }

}