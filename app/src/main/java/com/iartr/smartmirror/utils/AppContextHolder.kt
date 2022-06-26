package com.iartr.smartmirror.utils

import android.content.Context

object AppContextHolder {
    lateinit var context: Context

    fun isInitialized(): Boolean = ::context.isInitialized
}