package com.iartr.smartmirror.utils.progressdialog

import android.app.Activity
import android.app.ProgressDialog
import android.os.Handler
import android.os.Looper
import com.iartr.smartmirror.R
import com.iartr.smartmirror.utils.safeRun

class ProgressDialogImpl(
    activity: Activity
) {
    private val uiHandler: Handler = Handler(Looper.getMainLooper())

    private var progressDialog: ProgressDialog? = null

    init {
        uiHandler.post {
            progressDialog = ProgressDialog(activity)
            progressDialog?.setMessage(activity.getString(R.string.loading))
            progressDialog?.setCancelable(false)
            progressDialog?.setCanceledOnTouchOutside(false)
        }
    }

    fun show(delay: Long = 0L) = safeRun {
        if (delay > 0) {
            uiHandler.postDelayed({ show() }, delay)
        } else {
            uiHandler.post { show() }
        }
    }

    fun dismiss() = safeRun {
        uiHandler.removeCallbacksAndMessages(null)
        uiHandler.post {
            try {
                progressDialog?.dismiss()
            } catch (ignored: Exception) {
            }
            progressDialog = null
        }
    }

    private fun show() {
        if (progressDialog == null) return
        val context = progressDialog?.context?.toActivitySafe()
        if (context != null && !context.isFinishing && !context.isDestroyed) {
            progressDialog?.show()
        }
    }
}