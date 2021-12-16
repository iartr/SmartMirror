package com.iartr.smartmirror.data.articles

interface IArticlesRepository {
    fun getArticlePreviews(day: Any, offset: Int, limit: Int): List<Any>

    fun getFullArticle(articleId: Int): List<Any>
}