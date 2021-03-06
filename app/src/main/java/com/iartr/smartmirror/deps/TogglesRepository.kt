package com.iartr.smartmirror.deps

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.iartr.smartmirror.toggles.ITogglesRepository
import com.iartr.smartmirror.toggles.TogglesSet
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class TogglesRepository(
    private val fbRemoteConfig: FirebaseRemoteConfig,
    private val fbUserDatabase: () -> DatabaseReference,
    private val preferences: SharedPreferences
) : ITogglesRepository {
    override fun isEnabled(toggle: TogglesSet): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            val remoteConfigValue = fbRemoteConfig.getBoolean(toggle.asString)
            val preferenceValue = preferences.getBoolean(toggle.asString, toggle.defaultEnabled)
            fbUserDatabase().child(toggle.asString).get()
                .addOnSuccessListener {
                    val dbValue = it.getValue<Boolean>() ?: toggle.defaultEnabled
                    emitter.onSuccess(remoteConfigValue && preferenceValue && dbValue)
                }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    override fun setEnabled(toggle: TogglesSet, isEnabled: Boolean): Completable {
        return Completable.create { emitter ->
            fbUserDatabase().child(toggle.asString).setValue(isEnabled)
                .addOnSuccessListener {
                    preferences.edit { putBoolean(toggle.asString, isEnabled) }
                    emitter.onComplete()
                }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }
}