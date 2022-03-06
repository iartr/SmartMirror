package com.iartr.smartmirror.ui.account

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iartr.smartmirror.R
import com.iartr.smartmirror.ui.base.BaseFragment

class AccountFragment : BaseFragment(R.layout.fragment_account) {

    private val firebaseAuthStateListener: FirebaseAuth.AuthStateListener = object : FirebaseAuth.AuthStateListener {
        override fun onAuthStateChanged(auth: FirebaseAuth) {
            // Right after the listener has been registered
            // When a user is signed in
            // When the current user is signed out
            // When the current user changes

            if (auth.currentUser == null) {
                Toast.makeText(requireContext(), R.string.you_was_sign_out, Toast.LENGTH_SHORT).show()
                back()
            }
        }
    }
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener)
        val user = firebaseAuth.currentUser ?: return
        view.findViewById<ImageView>(R.id.account_photo).apply {
            Glide.with(this).load(user.photoUrl).into(this)
        }
        view.findViewById<TextView>(R.id.account_uid).apply {
            text = getString(R.string.account_uid, user.uid)
        }
        view.findViewById<TextView>(R.id.account_email).apply {
            text = getString(R.string.account_email, user.email)
        }
        view.findViewById<TextView>(R.id.account_is_email_verified).apply {
            text = getString(R.string.account_email_is_verified, user.isEmailVerified.toString())
        }
        view.findViewById<TextView>(R.id.account_display_name).apply {
            text = getString(R.string.account_display_name, user.displayName)
        }
        view.findViewById<TextView>(R.id.account_phone).apply {
            text = getString(R.string.account_phone, user.phoneNumber)
        }
        view.findViewById<TextView>(R.id.account_multifactor).apply {
            text = getString(R.string.account_multifactor, user.multiFactor)
        }
        view.findViewById<TextView>(R.id.account_provider_id).apply {
            text = getString(R.string.account_provider_id, user.providerId)
        }
        view.findViewById<TextView>(R.id.account_tenat_id).apply {
            text = getString(R.string.account_tenat_id, user.tenantId)
        }
        view.findViewById<Button>(R.id.account_sign_out_button).apply {
            setOnClickListener { firebaseAuth.signOut() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firebaseAuth.removeAuthStateListener(firebaseAuthStateListener)
    }

    private fun back() = activity?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

    companion object {
        fun newInstance() = AccountFragment()
    }
}