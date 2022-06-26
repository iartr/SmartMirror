package com.iartr.smartmirror.ui.account

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.iartr.smartmirror.data.account.Account
import io.reactivex.rxjava3.core.Single

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

    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    fun logout() {
        firebaseAuth.signOut()
    }

    fun authByGoogle(data: Intent?): Single<AuthResult> {
        return Single.create { emitter ->
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener { googleAccount ->
                    val googleCredentials = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
                    Firebase.auth.signInWithCredential(googleCredentials)
                        .addOnSuccessListener(emitter::onSuccess)
                        .addOnFailureListener(emitter::tryOnError)
                }
                .addOnFailureListener(emitter::tryOnError)
        }
    }
}