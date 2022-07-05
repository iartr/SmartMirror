package com.iartr.smartmirror.toggles

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

lateinit var togglesRepositoryProvider: Lazy<ITogglesRepository>
val togglesRepository: ITogglesRepository
    get() = togglesRepositoryProvider.value

interface ITogglesRepository {
    fun isEnabled(toggle: TogglesSet): Single<Boolean>

    fun setEnabled(toggle: TogglesSet, isEnabled: Boolean): Completable
}