package com.iartr.smartmirror.accountsettings.api

import androidx.fragment.app.Fragment
import com.iartr.smartmirror.accountsettings.AccountSettingsFragment

class AccountSettingsApi {
    fun fragment(): Fragment = AccountSettingsFragment.newInstance()
}