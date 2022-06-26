package com.iartr.smartmirror.ui.base

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.iartr.smartmirror.utils.ActivityHelper

open class BaseActivity(@LayoutRes layout: Int) : AppCompatActivity(layout) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        ActivityHelper.onNewIntent(this)
    }

}