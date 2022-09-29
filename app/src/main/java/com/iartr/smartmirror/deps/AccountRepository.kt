package com.iartr.smartmirror.deps

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iartr.smartmirror.account.Account
import com.iartr.smartmirror.account.AuthState
import com.iartr.smartmirror.account.AuthStateListener
import com.iartr.smartmirror.account.IAccountRepository
import com.iartr.smartmirror.core.utils.AppContextHolder
import com.iartr.smartmirror.R
import com.iartr.smartmirror.core.utils.dagger.AppScope
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@AppScope
class AccountRepository @Inject constructor(
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

    override val google: IAccountRepository.Google = Google()

    inner class Google : IAccountRepository.Google {
        override fun getIntentForAuth(): Intent {
            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AppContextHolder.context.getString(R.string.server_client_id_google))
                .requestEmail()
                .build()

            return GoogleSignIn.getClient(AppContextHolder.context, googleSignInOptions).signInIntent
        }

        override fun auth(data: Intent?): Single<Boolean> {
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