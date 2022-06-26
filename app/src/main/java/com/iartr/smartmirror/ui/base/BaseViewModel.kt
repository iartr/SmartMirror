package com.iartr.smartmirror.ui.base

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.iartr.smartmirror.R
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject

open class BaseViewModel : ViewModel() {
    open val router: BaseRouter = BaseRouter()

    protected val disposables = CompositeDisposable()

    private val isProgressDialogVisibleMutable: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    val isProgressDialogVisible: Observable<Boolean> = isProgressDialogVisibleMutable.distinctUntilChanged()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    protected fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    protected fun <T> Single<T>.withProgressDialog(): Single<T> {
        return doOnSubscribe { isProgressDialogVisibleMutable.onNext(true) }
            .doFinally { isProgressDialogVisibleMutable.onNext(false) }
    }

    protected fun <T> Observable<T>.withProgressDialog(): Observable<T> {
        return doOnSubscribe { isProgressDialogVisibleMutable.onNext(true) }
            .doFinally { isProgressDialogVisibleMutable.onNext(false) }
    }

    protected fun Completable.withProgressDialog(): Completable {
        return doOnSubscribe { isProgressDialogVisibleMutable.onNext(true) }
            .doFinally { isProgressDialogVisibleMutable.onNext(false) }
    }

    protected fun <T> Single<T>.withErrorDisplay(@StringRes stringRes: Int = R.string.common_network_error): Single<T> {
        return doOnError { router.showToast(stringRes) }
    }

    protected fun <T> Observable<T>.withErrorDisplay(@StringRes stringRes: Int = R.string.common_network_error): Observable<T> {
        return doOnError { router.showToast(stringRes) }
    }

    protected fun Completable.withErrorDisplay(@StringRes stringRes: Int = R.string.common_network_error): Completable {
        return doOnError { router.showToast(stringRes) }
    }
}