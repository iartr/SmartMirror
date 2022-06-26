package com.iartr.smartmirror.utils.progressdialog

import android.app.Activity

class ProgressDialogHolder(private val provideActivity: () -> Activity?) {

    private var progressDialog: ProgressDialogImpl? = null

    fun setVisible(shouldBeVisible: Boolean) {
        val activity = provideActivity()
        if (progressDialog == null && shouldBeVisible) {
            if (activity == null || activity.isFinishing || activity.isDestroyed) {
                return
            }

            progressDialog = ProgressDialogImpl(activity).apply {
                show()
            }
        }
        if (progressDialog != null && !shouldBeVisible) {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }
}