package com.iartr.smartmirror.account

import android.content.Intent
import io.reactivex.rxjava3.core.Single

lateinit var accountRepositoryProvider: Lazy<IAccountRepository>
val accountRepository: IAccountRepository
    get() = accountRepositoryProvider.value

interface IAccountRepository {
    fun addAuthStateListener(listener: AuthStateListener)

    fun removeAuthStateListener(listener: AuthStateListener)

    fun authByGoogle(data: Intent?): Single<Boolean>

    fun getAccountInfo(): Account?

    fun isLoggedIn(): Boolean

    fun logout()
}

fun interface AuthStateListener {
    fun onChanged(state: AuthState)
}

sealed class AuthState(val isLoggedIn: Boolean) {
    object Logout : AuthState(isLoggedIn = false)

    data class Logged(val account: Account) : AuthState(isLoggedIn = true)
}