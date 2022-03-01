package com.iartr.smartmirror.toggles

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class CameraFeatureToggle(private val context: Context) : FeatureToggle {

    override fun isActive(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_CAMERA_ACTIVE, false)
    }

    override fun setActive(isActive: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit { putBoolean(IS_CAMERA_ACTIVE, isActive) }
    }

    private companion object {
        private const val IS_CAMERA_ACTIVE = "pref_camera_is_active"
    }
}