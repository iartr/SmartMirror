package com.iartr.smartmirror.core.utils

import android.content.Context

object AppContextHolder {
    lateinit var context: Context

    fun isInitialized(): Boolean = AppContextHolder::context.isInitialized
}