package com.iartr.smartmirror.ui.main

import com.iartr.smartmirror.ui.account.AccountFragment
import com.iartr.smartmirror.ui.base.BaseRouter
import com.iartr.smartmirror.ui.debug.PreferenceActivity

class MainRouter : BaseRouter() {
    fun openDebug() {
        openActivity(PreferenceActivity::class.java)
    }

    fun openAccount() {
        openFragment(AccountFragment.newInstance())
    }
}