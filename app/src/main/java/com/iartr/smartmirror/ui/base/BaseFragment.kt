package com.iartr.smartmirror.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.iartr.smartmirror.utils.progressdialog.ProgressDialogHolder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo

abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {
    private val fragmentDisposables = CompositeDisposable()

    private val progressDialog: ProgressDialogHolder by lazy { ProgressDialogHolder { activity } }

    abstract val viewModel: BaseViewModel

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isProgressDialogVisible.subscribeWithFragment(progressDialog::setVisible)
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        fragmentDisposables.clear()
    }

    protected fun <T> Observable<T>.subscribeWithFragment(onNext: (T) -> Unit) {
        this
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext, { android.util.Log.e("Observable_onError at ${this.javaClass.simpleName}", "An error occurred", it) }, {  })
            .addTo(fragmentDisposables)
    }
}