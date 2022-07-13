package com.iartr.smartmirror.news.api

import androidx.recyclerview.widget.ListAdapter
import com.iartr.smartmirror.news.Article
import com.iartr.smartmirror.news.IArticlesRepository

lateinit var newsFeatureApiProvider: Lazy<NewsFeatureApi>

interface NewsFeatureApi {
    fun recyclerAdapter(): ListAdapter<Article, *>

    // singleton should be in future...
    fun articlesRepository(): IArticlesRepository
}