package com.iartr.smartmirror.ui.base

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo

open class BaseFragment(layoutId: Int) : Fragment(layoutId) {
    private val fragmentDisposables = CompositeDisposable()

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        fragmentDisposables.clear()
    }

    protected fun <T> Observable<T>.subscribeWithFragment(onNext: (T) -> Unit) {
        this
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext, { android.util.Log.e("Observable_onError", "", it) }, {  })
            .addTo(fragmentDisposables)
    }
}