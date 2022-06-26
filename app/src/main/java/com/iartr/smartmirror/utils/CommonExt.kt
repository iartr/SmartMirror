package com.iartr.smartmirror.utils

inline fun safeRun(block: () -> Unit) = try {
    block()
} catch (th: Throwable) {
    android.util.Log.e("TODO: Logger", "An error occurred", th)
}