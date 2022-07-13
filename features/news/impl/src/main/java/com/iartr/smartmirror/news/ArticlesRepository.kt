package com.iartr.smartmirror.news

import io.reactivex.rxjava3.core.Single

internal class ArticlesRepository(
    private val api: NewsApi
): IArticlesRepository {

    override fun getLatest(): Single<List<Article>> {
        return api.getEverything(query = "Mobile development")
            .map { it.articles.take(2) }
    }

}