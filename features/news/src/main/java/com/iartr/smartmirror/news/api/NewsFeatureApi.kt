package com.iartr.smartmirror.news.api

import androidx.recyclerview.widget.ListAdapter
import com.iartr.smartmirror.network.retrofitApi
import com.iartr.smartmirror.news.Article
import com.iartr.smartmirror.news.ArticlesAdapter
import com.iartr.smartmirror.news.ArticlesRepository
import com.iartr.smartmirror.news.IArticlesRepository
import com.iartr.smartmirror.news.NewsApi

class NewsFeatureApi {
    fun recyclerAdapter(): ListAdapter<Article, *> {
        return ArticlesAdapter()
    }

    fun repository(): IArticlesRepository {
        return ArticlesRepository(api = newsRemoteDataSource())
    }

    private fun newsRemoteDataSource(): NewsApi {
        return retrofitApi("https://newsapi.org/v2/")
    }
}