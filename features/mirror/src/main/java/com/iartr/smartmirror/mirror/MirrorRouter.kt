package com.iartr.smartmirror.mirror

import com.iartr.smartmirror.accountsettings.AccountSettingFeatureApi
import com.iartr.smartmirror.mvvm.BaseRouter
import javax.inject.Inject

class MirrorRouter @Inject constructor(
    private val accountSettingsFeatureApi: AccountSettingFeatureApi
) : BaseRouter() {
    fun openAccount() {
        val fragment = accountSettingsFeatureApi.fragment()
        openFragment(fragment)
    }
}