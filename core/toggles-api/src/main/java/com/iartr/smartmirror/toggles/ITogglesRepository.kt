package com.iartr.smartmirror.toggles

import kotlinx.coroutines.flow.Flow

interface ITogglesRepository {
    fun isEnabled(toggle: TogglesSet): Flow<Boolean>
    fun setEnabled(toggle: TogglesSet, isEnabled: Boolean): Flow<Unit>
}