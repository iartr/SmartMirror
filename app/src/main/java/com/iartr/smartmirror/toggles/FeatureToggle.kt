package com.iartr.smartmirror.toggles

interface FeatureToggle {
    fun isActive(): Boolean
    fun setActive(isActive: Boolean)
}