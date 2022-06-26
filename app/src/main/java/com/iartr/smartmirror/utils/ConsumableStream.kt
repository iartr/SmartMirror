package com.iartr.smartmirror.utils

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * Stream with consumable caching:
 * - Values caches until someone subscribes to it
 * - Values consumes after someone receive them with subscription (both cached and ongoing)
 */
class ConsumableStream<T>() {

    private val subj = BehaviorSubject.createDefault<List<T>>(listOf())

    @Synchronized
    fun push(value: T) {
        subj.onNext(subj.value + value)
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