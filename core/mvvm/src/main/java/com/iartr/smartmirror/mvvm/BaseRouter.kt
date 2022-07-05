package com.iartr.smartmirror.mvvm

import android.app.Activity
import android.content.Intent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.iartr.smartmirror.core.utils.ActivityHelper
import com.iartr.smartmirror.core.utils.ToastUtils

open class BaseRouter {
    private val currentActivity: AppCompatActivity?
        get() = ActivityHelper.getCurrentCreatedActivity()

    protected fun withActivity(action: (activity: AppCompatActivity) -> Unit): Unit? {
        return currentActivity?.let(action)
    }

    protected fun openActivity(activityClass: Class<out Activity>) {
        withActivity {
            it.startActivity(Intent(it, activityClass))
        }
    }

    protected fun openFragment(fragment: Fragment) {
        withActivity {
            val fm = it.supportFragmentManager
            if (fm.isStateSaved) {
                return@withActivity
            }

            fm.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    fun back() = withActivity { it.onBackPressed() }

    fun showToast(@StringRes text: Int) = withActivity { ToastUtils.showToast(text) }

    fun showToast(text: String, isLongDuration: Boolean = false) = withActivity {
        ToastUtils.showToast(text, isLongDuration)
    }
}