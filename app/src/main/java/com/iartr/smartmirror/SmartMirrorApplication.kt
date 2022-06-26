package com.iartr.smartmirror

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.iartr.smartmirror.utils.ActivityHelper
import com.iartr.smartmirror.utils.AppContextHolder

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
}