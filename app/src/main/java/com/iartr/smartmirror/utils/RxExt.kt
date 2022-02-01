package com.iartr.smartmirror.utils

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable

fun <T> Single<T>.subscribeSuccess(onSuccess: (T) -> Unit): Disposable {
    return this.subscribe({ onSuccess(it) }, { android.util.Log.e("Single_onError", "", it) })
}

fun <T> Observable<T>.subscribeSuccess(onNext: (T) -> Unit): Disposable {
    return this.subscribe(
        { onNext(it) },
        { android.util.Log.e("Observable_onError", "", it) },
        {  }
    )
}