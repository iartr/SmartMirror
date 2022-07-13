package com.iartr.smartmirror.core.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

object ActivityHelper {

    private var resumedActivity: WeakReference<Activity?> = WeakReference(null)

    private var createdActivity: WeakReference<Activity?> = WeakReference(null)

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks() {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                createdActivity = WeakReference(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                resumedActivity = WeakReference(activity)
                createdActivity = resumedActivity
            }
        })
    }

    fun onNewIntent(activity: Activity) {
        createdActivity = WeakReference(activity)
    }

    fun getCurrentCreatedActivity() = createdActivity.get()
}