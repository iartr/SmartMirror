package com.iartr.smartmirror.news

import androidx.recyclerview.widget.ListAdapter

interface NewsFeatureApi {
    fun recyclerAdapter(): ListAdapter<News, *>

    fun newsRepository(): INewsRepository
}