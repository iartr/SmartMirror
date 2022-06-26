package com.iartr.smartmirror.ui.base

import android.app.Activity
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.iartr.smartmirror.R
import com.iartr.smartmirror.utils.ActivityHelper
import com.iartr.smartmirror.utils.ToastUtils

open class BaseRouter {
    private val currentActivity: AppCompatActivity?
        get() = ActivityHelper.getCurrentCreatedActivity()

    protected fun withActivity(action: (activity: AppCompatActivity) -> Unit): Unit? {
        return currentActivity?.let(action)
    }

    protected fun openFragment(fragment: Fragment) {
        withActivity {
            val fm = it.supportFragmentManager
            if (fm.isStateSaved) {
                return@withActivity
            }

            fm.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit()
        }
    }

    fun back() = withActivity { it.onBackPressed() }

    fun showToast(@StringRes text: Int) = withActivity { ToastUtils.showToast(text) }

    fun showToast(text: String, isLongDuration: Boolean = false) = withActivity {
        ToastUtils.showToast(text, isLongDuration)
    }
}