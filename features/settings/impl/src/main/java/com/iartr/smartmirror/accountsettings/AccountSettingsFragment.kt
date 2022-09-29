package com.iartr.smartmirror.accountsettings

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import com.iartr.smartmirror.accountsettings.di.accountSettingsFeatureComponentProvider
import com.iartr.smartmirror.design.RetryingErrorView
import com.iartr.smartmirror.mvvm.BaseFragment
import com.iartr.smartmirror.accountsettings.impl.R
import dagger.Lazy
import javax.inject.Inject

class AccountSettingsFragment : BaseFragment(R.layout.fragment_account) {

    private lateinit var errorView: RetryingErrorView
    private lateinit var progressBar: ProgressBar
    private lateinit var contentRoot: View
    private lateinit var toolbar: Toolbar
    private lateinit var photoIv: ImageView
    private lateinit var uidTv: TextView
    private lateinit var emailTv: TextView
    private lateinit var isEmailVerifiedTv: TextView
    private lateinit var displayNameTv: TextView
    private lateinit var phoneTv: TextView
    private lateinit var cameraCheckBox: CheckBox
    private lateinit var adsCheckBox: CheckBox
    private lateinit var articlesCheckBox: CheckBox
    private lateinit var currencyCheckBox: CheckBox
    private lateinit var weatherCheckBox: CheckBox
    private lateinit var logoutButton: Button
    private val errorAccountDrawable: Drawable? by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_account_circle_outline_24)?.apply {
            colorFilter = PorterDuffColorFilter(MaterialColors.getColor(requireView(), R.attr.background_highlighted), PorterDuff.Mode.SRC_ATOP)
        }
    }

    @Inject
    internal lateinit var viewModelFactory: Lazy<AccountSettingsViewModel.Factory>

    override val viewModel: AccountSettingsViewModel by viewModels(
        factoryProducer = { viewModelFactory.get() }
    )

    override fun onAttach(context: Context) {
        accountSettingsFeatureComponentProvider.value.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorView = view.findViewById(R.id.account_content_retrying_view)
        progressBar = view.findViewById(R.id.account_progress)
        contentRoot = view.findViewById(R.id.account_content_root)
        toolbar = view.findViewById<Toolbar>(R.id.account_toolbar).apply {
            setNavigationOnClickListener { viewModel.onBack() }
        }
        photoIv = view.findViewById<ImageView>(R.id.account_photo)
        uidTv = view.findViewById<TextView>(R.id.account_uid)
        emailTv = view.findViewById<TextView>(R.id.account_email)
        isEmailVerifiedTv = view.findViewById<TextView>(R.id.account_is_email_verified)
        displayNameTv = view.findViewById<TextView>(R.id.account_display_name)
        phoneTv = view.findViewById<TextView>(R.id.account_phone)
        cameraCheckBox = view.findViewById<CheckBox>(R.id.account_camera_checkbox).apply {
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.onCameraChecked(isChecked)
            }
        }
        adsCheckBox = view.findViewById<CheckBox>(R.id.account_ads_checkbox).apply {
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.onAdsChecked(isChecked)
            }
        }
        articlesCheckBox = view.findViewById<CheckBox>(R.id.account_articles_checkbox).apply {
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.onArticlesChecked(isChecked)
            }
        }
        currencyCheckBox = view.findViewById<CheckBox>(R.id.account_currencies_checkbox).apply {
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.onCurrencyChecked(isChecked)
            }
        }
        weatherCheckBox = view.findViewById<CheckBox>(R.id.account_weather_checkbox).apply {
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.onWeatherChecked(isChecked)
            }
        }
        logoutButton = view.findViewById<Button>(R.id.account_sign_out_button).apply {
            setOnClickListener { viewModel.onLogoutClicked() }
        }

        viewModel.viewState.subscribeWithFragment { state ->
            when (state) {
                is AccountSettingsViewModel.State.Content -> {
                    contentRoot.isInvisible = false
                    progressBar.isVisible = false
                    errorView.hide()

                    Glide.with(this)
                        .load(state.accountInfo.photoUrl)
                        .placeholder(errorAccountDrawable)
                        .fallback(errorAccountDrawable)
                        .error(errorAccountDrawable)
                        .into(photoIv)

                    uidTv.text = state.accountInfo.uid
                    displayNameTv.isVisible = state.accountInfo.displayName != null
                    displayNameTv.text = state.accountInfo.displayName
                    phoneTv.isVisible = state.accountInfo.phone != null
                    phoneTv.text = state.accountInfo.phone
                    emailTv.isVisible = state.accountInfo.email != null
                    emailTv.text = state.accountInfo.email
                    isEmailVerifiedTv.isVisible = state.accountInfo.email != null
                    isEmailVerifiedTv.text = state.accountInfo.isEmailVerified.toString()

                    cameraCheckBox.isChecked = state.features.isCameraEnabled
                    adsCheckBox.isChecked = state.features.isAdsEnabled
                    articlesCheckBox.isChecked = state.features.isArticlesEnabled
                    weatherCheckBox.isChecked = state.features.isWeatherEnabled
                    currencyCheckBox.isChecked = state.features.isCurrencyEnabled
                }
                is AccountSettingsViewModel.State.Error -> {
                    contentRoot.isInvisible = true
                    progressBar.isVisible = false
                    errorView.show(retryAction = { viewModel.loadFeatures() })
                }
                is AccountSettingsViewModel.State.Loading -> {
                    contentRoot.isInvisible = true
                    progressBar.isVisible = true
                    errorView.hide()
                }
            }
        }
    }

    companion object {
        fun newInstance() = AccountSettingsFragment()
    }
}