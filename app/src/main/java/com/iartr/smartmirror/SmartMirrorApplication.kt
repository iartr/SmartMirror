package com.iartr.smartmirror

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.iartr.smartmirror.account.accountRepositoryProvider
import com.iartr.smartmirror.core.utils.ActivityHelper
import com.iartr.smartmirror.core.utils.AppContextHolder
import com.iartr.smartmirror.deps.AccountRepository
import com.iartr.smartmirror.deps.TogglesRepository
import com.iartr.smartmirror.toggles.togglesRepositoryProvider

class SmartMirrorApplication : Application() {

    init {
        AppContextHolder.context = this
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        MobileAds.initialize(this)
        ActivityHelper.init(this)

        loadRemoteConfig()
        initToggles()

        initAccountDeps()
    }

    private fun loadRemoteConfig() {
        Firebase.remoteConfig
            .apply {
                setConfigSettingsAsync(
                    remoteConfigSettings {
                        minimumFetchIntervalInSeconds = 3600
                        fetchTimeoutInSeconds = 10000
                    }
                )
            }
            .apply { setDefaultsAsync(R.xml.remote_config_defaults) }
            .fetchAndActivate()
    }

    private fun initToggles() {
        togglesRepositoryProvider = lazy {
            TogglesRepository(
                fbRemoteConfig = Firebase.remoteConfig,
                fbUserDatabase = { Firebase.database.reference.child("${Firebase.auth.uid}") },
                preferences = getSharedPreferences("preference_toggles", Context.MODE_PRIVATE),
            )
        }
    }

    private fun initAccountDeps() {
        accountRepositoryProvider = lazy {
            AccountRepository(firebaseAuth = FirebaseAuth.getInstance())
        }
    }
}