package com.iartr.smartmirror.mvvm

import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

open class BaseViewModel(
    open val router: BaseRouter = BaseRouter()
) : ViewModel() {
    private val isProgressDialogVisibleMutable = MutableStateFlow(false)
    val isProgressDialogVisible: Flow<Boolean> = isProgressDialogVisibleMutable.asStateFlow()

    @CallSuper
    override fun onCleared() {
        super.onCleared()
    }

    protected fun <T : Any> Flow<T>.withProgressDialog(): Flow<T> {
        return onStart { isProgressDialogVisibleMutable.emit(true) }
            .onCompletion { isProgressDialogVisibleMutable.emit(false) }
    }

    protected fun <T : Any> Flow<T>.withErrorDisplay(@StringRes stringRes: Int = com.iartr.smartmirror.design.R.string.common_network_error): Flow<T> {
        return catch { router.showToast(stringRes) }
    }
}