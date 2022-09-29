package com.iartr.smartmirror.accountsettings

import com.iartr.smartmirror.account.IAccountRepository
import com.iartr.smartmirror.toggles.ITogglesRepository

interface AccountSettingsFeatureDependencies {
    val togglesRepository: ITogglesRepository

    val accountRepository: IAccountRepository
}