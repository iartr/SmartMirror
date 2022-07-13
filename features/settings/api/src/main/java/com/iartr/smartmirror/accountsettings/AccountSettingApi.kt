package com.iartr.smartmirror.accountsettings

import androidx.fragment.app.Fragment

lateinit var accountSettingsApiProvider: Lazy<AccountSettingApi>

interface AccountSettingApi {
    fun fragment(): Fragment
}