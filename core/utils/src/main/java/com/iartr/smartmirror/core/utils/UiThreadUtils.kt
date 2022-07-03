package com.iartr.smartmirror.core.utils

import android.os.Handler
import android.os.Looper

object UiThreadUtils {
    @JvmStatic
    val handler by lazy { Handler(Looper.getMainLooper()) }

    @JvmStatic
    fun isMainThread() = Looper.getMainLooper() == Looper.myLooper()

    @JvmStatic
    fun runOnMainThread(runnable: Runnable, delay: Long) {
        if (Looper.myLooper() == Looper.getMainLooper() && delay == 0L) {
            runnable.run()
        } else {
            handler.postDelayed(runnable, delay)
        }
    }

    @JvmStatic
    fun postOnMainThread(runnable: Runnable) {
        handler.post(runnable)
    }

    @JvmStatic
    fun postOnMainThread(runnable: Runnable, delay: Long) {
        handler.postDelayed(runnable, delay)
    }
}