package com.iartr.smartmirror.ui.account

import com.google.firebase.auth.FirebaseAuth
import com.iartr.smartmirror.data.account.Account

class AccountRepository {
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.addAuthStateListener(listener)
    }

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.removeAuthStateListener(listener)
    }

    fun getAccountInfo(): Account? {
        return firebaseAuth.currentUser?.let { user ->
            Account(
                uid = user.uid,
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString(),
                email = user.email,
                isEmailVerified = user.isEmailVerified,
                phone = user.phoneNumber,
            )
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}