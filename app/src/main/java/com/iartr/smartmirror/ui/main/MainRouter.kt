package com.iartr.smartmirror.ui.main

import com.iartr.smartmirror.accountsettings.api.AccountSettingsApi
import com.iartr.smartmirror.mvvm.BaseRouter

class MainRouter : BaseRouter() {
    fun openAccount() {
        val featureApi = AccountSettingsApi()
        openFragment(featureApi.fragment())
    }
}