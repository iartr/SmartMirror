package com.iartr.smartmirror.account

import android.content.Intent
import kotlinx.coroutines.flow.Flow

interface IAccountRepository {
    fun addAuthStateListener(listener: AuthStateListener)

    fun removeAuthStateListener(listener: AuthStateListener)

    fun getAccountInfo(): Account?

    fun isLoggedIn(): Boolean

    fun logout()

    val google: Google

    interface Google {
        fun getIntentForAuth(): Intent?
        fun auth(data: Intent?): Flow<Unit>
    }
}

fun interface AuthStateListener {
    fun onChanged(state: AuthState)
}

sealed class AuthState(val isLoggedIn: Boolean) {
    object Logout : AuthState(isLoggedIn = false)

    data class Logged(val account: Account) : AuthState(isLoggedIn = true)
}