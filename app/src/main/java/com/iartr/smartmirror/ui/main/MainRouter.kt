package com.iartr.smartmirror.ui.main

import com.iartr.smartmirror.accountsettings.api.AccountSettingsApi
import com.iartr.smartmirror.mvvm.BaseRouter
import com.iartr.smartmirror.ui.debug.PreferenceActivity

class MainRouter : BaseRouter() {
    fun openDebug() {
        openActivity(PreferenceActivity::class.java)
    }

    fun openAccount() {
        val featureApi = AccountSettingsApi()
        openFragment(featureApi.fragment())
    }
}