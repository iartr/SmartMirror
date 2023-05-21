package com.iartr.smartmirror.mvvm

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.iartr.smartmirror.design.progressdialog.ProgressDialogHolder

abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {
    private val progressDialog: ProgressDialogHolder by lazy { ProgressDialogHolder { activity } }

    abstract val viewModel: BaseViewModel

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed { viewModel.isProgressDialogVisible.collect(progressDialog::setVisible) }
    }
}