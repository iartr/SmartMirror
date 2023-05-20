package com.iartr.smartmirror.news

import kotlinx.coroutines.flow.Flow

interface INewsRepository {
    fun getLatest(): Flow<List<News>>
}