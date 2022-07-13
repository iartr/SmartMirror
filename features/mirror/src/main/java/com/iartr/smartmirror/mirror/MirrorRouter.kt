package com.iartr.smartmirror.mirror

import com.iartr.smartmirror.accountsettings.accountSettingsApiProvider
import com.iartr.smartmirror.mvvm.BaseRouter

class MirrorRouter : BaseRouter() {
    fun openAccount() {
        val fragment = accountSettingsApiProvider.value.fragment()
        openFragment(fragment)
    }
}