package com.iartr.smartmirror.data.articles

import io.reactivex.rxjava3.core.Single

class ArticlesRepository(private val api: NewsApi): IArticlesRepository {

    override fun getLatest(): Single<List<Article>> {
        return api.getEverything(query = "Mobile development")
            .map { it.articles.take(2) }
    }

}