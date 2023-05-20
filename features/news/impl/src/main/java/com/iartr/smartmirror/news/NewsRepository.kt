package com.iartr.smartmirror.news

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class NewsRepository @Inject constructor(
    private val api: NewsApi
): INewsRepository {

    override fun getLatest(): Flow<List<News>> {
        return flow { emit(api.getEverything(query = "Mobile development")) }
            .map { it.articles.take(2) }
    }

}