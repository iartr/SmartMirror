package com.iartr.smartmirror.toggles

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

interface ITogglesRepository {
    fun isEnabled(toggle: TogglesSet): Single<Boolean>
    fun setEnabled(toggle: TogglesSet, isEnabled: Boolean): Completable

    fun isEnabled2(toggle: TogglesSet): Flow<Boolean>
    fun setEnabled2(toggle: TogglesSet, isEnabled: Boolean): Flow<Unit>
}