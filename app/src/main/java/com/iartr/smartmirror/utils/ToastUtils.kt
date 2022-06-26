package com.iartr.smartmirror.utils

import android.os.Looper
import android.widget.Toast
import androidx.annotation.UiThread

object ToastUtils {
    @JvmStatic
    @JvmOverloads
    fun showToast(resId: Int, isLongDuration: Boolean = false) = showToast(AppContextHolder.context.resources.getString(resId), isLongDuration)

    @JvmStatic
    @JvmOverloads
    fun showToast(text: CharSequence?, isLongDuration: Boolean = false) = showToastImpl(text, isLongDuration)

    @JvmStatic
    private fun showToastImpl(text: CharSequence?, isLongDuration: Boolean = false) {
        if (text == null) return

        val duration = if (isLongDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        if (Looper.myLooper() == Looper.getMainLooper()) {
            make(text, duration)
        } else {
            UiThreadUtils.postOnMainThread { make(text, duration) }
        }
    }

    @UiThread
    private fun make(text: CharSequence?, duration: Int) {
        val toast = Toast.makeText(AppContextHolder.context, text, duration)
        toast.show()
    }
}