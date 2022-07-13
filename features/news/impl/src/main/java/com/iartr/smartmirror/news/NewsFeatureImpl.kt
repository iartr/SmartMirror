package com.iartr.smartmirror.news

import androidx.recyclerview.widget.ListAdapter
import com.iartr.smartmirror.network.retrofitApi
import com.iartr.smartmirror.news.Article
import com.iartr.smartmirror.news.ArticlesAdapter
import com.iartr.smartmirror.news.ArticlesRepository
import com.iartr.smartmirror.news.IArticlesRepository
import com.iartr.smartmirror.news.api.NewsFeatureApi

class NewsFeatureImpl : NewsFeatureApi {
    override fun recyclerAdapter(): ListAdapter<Article, *> {
        return ArticlesAdapter()
    }

    override fun articlesRepository(): IArticlesRepository {
        return ArticlesRepository(api = retrofitApi("https://newsapi.org/v2/"))
    }
}