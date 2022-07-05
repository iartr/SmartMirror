package com.iartr.smartmirror.ext

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable

fun <T> Single<T>.subscribeSuccess(onSuccess: (T) -> Unit = {}): Disposable {
    return this.subscribe({ onSuccess(it) }, { android.util.Log.e("Single_onError", "An error occurred", it) })
}

fun <T> Observable<T>.subscribeSuccess(onNext: (T) -> Unit = {}): Disposable {
    return this.subscribe(
        { onNext(it) },
        { android.util.Log.e("Observable_onError", "An error occurred", it) },
        {  }
    )
}

fun Completable.subscribeSuccess(onComplete: () -> Unit = {}): Disposable {
    return this.subscribe({ onComplete() }, { android.util.Log.e("Completable_onError", "An error occurred", it) })
}