package com.iartr.smartmirror.ui.account

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.iartr.smartmirror.R
import com.iartr.smartmirror.toggles.*
import com.iartr.smartmirror.ui.base.BaseFragment
import com.iartr.smartmirror.utils.subscribeSuccess

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
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private val firebaseDatabase: DatabaseReference = Firebase.database.reference.child("${firebaseAuth.uid}")

    // TODO: vm
    private lateinit var cameraToggle: FeatureToggle
    private lateinit var cameraCheckBox: CheckBox

    private lateinit var adsToggle: FeatureToggle
    private lateinit var adsCheckBox: CheckBox

    private lateinit var articlesToggle: FeatureToggle
    private lateinit var articlesCheckBox: CheckBox

    private lateinit var currencyToggle: FeatureToggle
    private lateinit var currencyCheckBox: CheckBox

    private lateinit var weatherToggle: FeatureToggle
    private lateinit var weatherCheckBox: CheckBox

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener)
        val user = firebaseAuth.currentUser ?: return

        cameraToggle = CameraFeatureToggle(requireContext(), remoteConfig, firebaseDatabase)
        adsToggle = AdsFeatureToggle(remoteConfig, firebaseDatabase)
        articlesToggle = ArticlesFeatureToggle(remoteConfig, firebaseDatabase)
        currencyToggle = CurrencyFeatureToggle(remoteConfig, firebaseDatabase)
        weatherToggle = WeatherFeatureToggle(remoteConfig, firebaseDatabase)

        view.findViewById<Toolbar>(R.id.account_toolbar).apply {
            setNavigationOnClickListener { back() }
        }
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
        cameraCheckBox = view.findViewById<CheckBox>(R.id.account_camera_checkbox).apply {
//            isChecked = cameraToggle.isActive()
            setOnCheckedChangeListener { _, isChecked ->
                cameraToggle.setActive(isChecked).subscribe()
            }
        }
        adsCheckBox = view.findViewById<CheckBox>(R.id.account_ads_checkbox).apply {
//            isChecked = adsToggle.isActive()
            setOnCheckedChangeListener { _, isChecked ->
                adsToggle.setActive(isChecked).subscribe()
            }
        }
        articlesCheckBox = view.findViewById<CheckBox>(R.id.account_articles_checkbox).apply {
//            isChecked = articlesToggle.isActive()
            setOnCheckedChangeListener { _, isChecked ->
                articlesToggle.setActive(isChecked).subscribe()
            }
        }
        currencyCheckBox = view.findViewById<CheckBox>(R.id.account_currencies_checkbox).apply {
//            isChecked = currencyToggle.isActive()
            setOnCheckedChangeListener { _, isChecked ->
                currencyToggle.setActive(isChecked).subscribe()
            }
        }
        weatherCheckBox = view.findViewById<CheckBox>(R.id.account_weather_checkbox).apply {
//            isChecked = weatherToggle.isActive()
            setOnCheckedChangeListener { _, isChecked ->
                weatherToggle.setActive(isChecked).subscribe()
            }
        }
        view.findViewById<Button>(R.id.account_sign_out_button).apply {
            setOnClickListener { firebaseAuth.signOut() }
        }

        cameraToggle.isActive().subscribeSuccess(cameraCheckBox::setChecked)
        adsToggle.isActive().subscribeSuccess(adsCheckBox::setChecked)
        articlesToggle.isActive().subscribeSuccess(articlesCheckBox::setChecked)
        currencyToggle.isActive().subscribeSuccess(currencyCheckBox::setChecked)
        weatherToggle.isActive().subscribeSuccess(weatherCheckBox::setChecked)
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