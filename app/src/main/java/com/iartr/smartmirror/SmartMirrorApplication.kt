package com.iartr.smartmirror

import android.app.Application
import com.google.android.gms.ads.MobileAds

class SmartMirrorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }

}