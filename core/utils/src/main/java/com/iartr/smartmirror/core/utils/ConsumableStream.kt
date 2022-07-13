package com.iartr.smartmirror.core.utils

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * Stream with consumable caching:
 * - Values caches until someone subscribes to it
 * - Values consumes after someone receive them with subscription (both cached and ongoing)
 */
class ConsumableStream<T : Any>() {

    private val subj = BehaviorSubject.createDefault<List<T>>(listOf())

    @Synchronized
    fun push(value: T) {
        val curr = subj.value ?: listOf()
        subj.onNext(curr + value)
    }

    fun observe(): Observable<T> = subj
        .filter { it.isNotEmpty() }
        .doOnNext { clean() }
        .concatMapIterable { it }

    @Synchronized
    private fun clean() {
        subj.onNext(listOf())
    }
}