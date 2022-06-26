package com.iartr.smartmirror.utils.progressdialog

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.toActivitySafe(): Activity? {
    var context = this
    while (context !is Activity && context is ContextWrapper) context = context.baseContext
    return if (context is Activity) context else null
}