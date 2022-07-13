package com.iartr.smartmirror.accountsettings

import androidx.fragment.app.Fragment
import com.iartr.smartmirror.accountsettings.AccountSettingApi
import com.iartr.smartmirror.accountsettings.AccountSettingsFragment

class AccountSettingsFeatureImpl : AccountSettingApi {
    override fun fragment(): Fragment {
        return AccountSettingsFragment()
    }
}