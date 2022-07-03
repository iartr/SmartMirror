package com.iartr.smartmirror.design.progressdialog

import android.app.Activity
import android.app.ProgressDialog
import android.os.Handler
import android.os.Looper
import com.iartr.smartmirror.design.R

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

    fun show(delay: Long = 0L) = try {
        if (delay > 0) {
            uiHandler.postDelayed({ show() }, delay)
        } else {
            uiHandler.post { show() }
        }
    } catch (th: Throwable) { android.util.Log.e("ProgressDialogImpl", "An error occurred", th) }

    fun dismiss() = try {
        uiHandler.removeCallbacksAndMessages(null)
        uiHandler.post {
            try {
                progressDialog?.dismiss()
            } catch (ignored: Exception) {
            }
            progressDialog = null
        }
    } catch (th: Throwable) { android.util.Log.e("ProgressDialogImpl", "An error occurred", th) }

    private fun show() {
        if (progressDialog == null) return
        val context = (progressDialog?.context as? Activity)
        if (context != null && !context.isFinishing && !context.isDestroyed) {
            progressDialog?.show()
        }
    }
}