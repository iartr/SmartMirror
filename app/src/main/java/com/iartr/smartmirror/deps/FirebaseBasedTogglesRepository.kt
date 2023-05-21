package com.iartr.smartmirror.deps

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.iartr.smartmirror.core.utils.dagger.AppScope
import com.iartr.smartmirror.di.PrefsToggles
import com.iartr.smartmirror.toggles.ITogglesRepository
import com.iartr.smartmirror.toggles.TogglesSet
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@AppScope
class FirebaseBasedTogglesRepository @Inject constructor(
    private val fbRemoteConfig: FirebaseRemoteConfig,
    private val fbUserDatabase: Function0<@JvmSuppressWildcards DatabaseReference>,
    @PrefsToggles private val preferences: SharedPreferences,
) : ITogglesRepository {
    override fun isEnabled(toggle: TogglesSet): Flow<Boolean> {
        return callbackFlow {
            val remoteConfigValue = fbRemoteConfig.getBoolean(toggle.asString)
            val preferenceValue = preferences.getBoolean(toggle.asString, toggle.defaultEnabled)
            fbUserDatabase().child(toggle.asString).get()
                .addOnSuccessListener {
                    val dbValue = it.getValue<Boolean>() ?: toggle.defaultEnabled
                    trySend(remoteConfigValue && preferenceValue && dbValue)
                    close()
                }
                .addOnFailureListener { close(it) }

            awaitClose()
        }
    }

    override fun setEnabled(toggle: TogglesSet, isEnabled: Boolean): Flow<Unit> {
        return callbackFlow {
            fbUserDatabase().child(toggle.asString).setValue(isEnabled)
                .addOnSuccessListener {
                    preferences.edit { putBoolean(toggle.asString, isEnabled) }
                    trySendBlocking(Unit)
                    close()
                }
                .addOnFailureListener { close(it) }

            awaitClose()
        }
    }
}