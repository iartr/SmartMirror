package com.iartr.smartmirror.deps

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iartr.smartmirror.account.Account
import com.iartr.smartmirror.account.AuthState
import com.iartr.smartmirror.account.AuthStateListener
import com.iartr.smartmirror.account.IAccountRepository
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.ConcurrentHashMap

class AccountRepository(
    private val firebaseAuth: FirebaseAuth
): IAccountRepository {
    private val listeners: MutableMap<AuthStateListener, FirebaseAuth.AuthStateListener> = ConcurrentHashMap()

    override fun addAuthStateListener(listener: AuthStateListener) {
        val firebaseListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            val authState = if (user != null) {
                AuthState.Logged(user.map())
            } else {
                AuthState.Logout
            }
            listener.onChanged(authState)
        }

        listeners[listener] = firebaseListener
        firebaseAuth.addAuthStateListener(firebaseListener)
    }

    override fun removeAuthStateListener(listener: AuthStateListener) {
        val firebaseListener = listeners.get(listener) ?: return
        listeners.remove(listener)
        firebaseAuth.removeAuthStateListener(firebaseListener)
    }

    override fun getAccountInfo(): Account? {
        return firebaseAuth.currentUser?.map()
    }

    override fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun authByGoogle(data: Intent?): Single<Boolean> {
        return Single.create { emitter ->
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener { googleAccount ->
                    val googleCredentials = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
                    Firebase.auth.signInWithCredential(googleCredentials)
                        .addOnSuccessListener { emitter.onSuccess(true) }
                        .addOnFailureListener(emitter::tryOnError)
                }
                .addOnFailureListener(emitter::tryOnError)
        }
    }

    private fun FirebaseUser.map(): Account {
        return Account(
            uid = this.uid,
            displayName = this.displayName,
            photoUrl = this.photoUrl?.toString(),
            email = this.email,
            isEmailVerified = this.isEmailVerified,
            phone = this.phoneNumber,
        )
    }
}