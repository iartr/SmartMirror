package com.iartr.smartmirror.toggles

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface FeatureToggle {
    fun isActive(): Single<Boolean>
    fun setActive(isActive: Boolean): Completable
}