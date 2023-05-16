@file:Suppress("UNCHECKED_CAST")

package com.iartr.smartmirror.accountsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iartr.smartmirror.account.Account
import com.iartr.smartmirror.account.AuthStateListener
import com.iartr.smartmirror.account.IAccountRepository
import com.iartr.smartmirror.accountsettings.impl.R
import com.iartr.smartmirror.ext.subscribeSuccess
import com.iartr.smartmirror.mvvm.BaseViewModel
import com.iartr.smartmirror.toggles.ITogglesRepository
import com.iartr.smartmirror.toggles.TogglesSet
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class AccountSettingsViewModel(
    private val togglesRepository: ITogglesRepository,
    private val accountRepository: IAccountRepository,
    override val router: AccountRouter
) : BaseViewModel(router) {

    private val authStateListener = AuthStateListener {
        if (it.isLoggedIn) {
            return@AuthStateListener
        }

        router.showToast(com.iartr.smartmirror.account.R.string.you_was_sign_out)
        router.back()
    }

    private val viewStateMutable: BehaviorSubject<State> = BehaviorSubject.createDefault(State.Loading)
    val viewState: Observable<State> = viewStateMutable.distinctUntilChanged()

    private var content: State.Content = State.Content(
        accountInfo = Account(
            uid = "",
            displayName = "",
            photoUrl = "",
            email = "",
            isEmailVerified = false,
            phone = null,
        ),
        features = State.Features(
            isCameraEnabled = TogglesSet.CAMERA.defaultEnabled,
            isAdsEnabled = TogglesSet.ADS.defaultEnabled,
            isArticlesEnabled = TogglesSet.ARTICLES.defaultEnabled,
            isCurrencyEnabled = TogglesSet.CURRENCY.defaultEnabled,
            isWeatherEnabled = TogglesSet.WEATHER.defaultEnabled,
        )
    )
        set(value) {
            val withAccountInfo = accountRepository.getAccountInfo()?.let { value.copy(accountInfo = it) } ?: value
            field = withAccountInfo
            viewStateMutable.onNext(withAccountInfo)
        }

    init {
        loadFeatures()
        accountRepository.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        accountRepository.removeAuthStateListener(authStateListener)
    }

    fun loadFeatures() {
        val cameraSource = togglesRepository.isEnabled(TogglesSet.CAMERA)
        val adsSource = togglesRepository.isEnabled(TogglesSet.ADS)
        val articlesSource = togglesRepository.isEnabled(TogglesSet.ARTICLES)
        val currencySource = togglesRepository.isEnabled(TogglesSet.CURRENCY)
        val weatherSource = togglesRepository.isEnabled(TogglesSet.WEATHER)
        Single.zip(cameraSource, adsSource, articlesSource, currencySource, weatherSource) { camera, ads, articles, currency, weather ->
            State.Features(camera, ads, articles, currency, weather)
        }
            .doOnSubscribe { viewStateMutable.onNext(State.Loading) }
            .withErrorDisplay()
            .doOnError { viewStateMutable.onNext(State.Error) }
            .subscribeSuccess { content = content.copy(features = it) }
            .addTo(disposables)
    }

    fun onBack() = router.back()

    fun onLogoutClicked() {
        accountRepository.logout()
    }

    fun onCameraChecked(checked: Boolean) {
        togglesRepository.setEnabled(TogglesSet.CAMERA, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .subscribeSuccess {
                content = content.copy(features = content.features.copy(isCameraEnabled = checked))
            }
            .addTo(disposables)
    }

    fun onAdsChecked(checked: Boolean) {
        togglesRepository.setEnabled(TogglesSet.ADS, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .subscribeSuccess {
                content = content.copy(features = content.features.copy(isAdsEnabled = checked))
            }
            .addTo(disposables)
    }

    fun onArticlesChecked(checked: Boolean) {
        togglesRepository.setEnabled(TogglesSet.ARTICLES, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .subscribeSuccess {
                content = content.copy(features = content.features.copy(isArticlesEnabled = checked))
            }
            .addTo(disposables)
    }

    fun onCurrencyChecked(checked: Boolean) {
        togglesRepository.setEnabled(TogglesSet.CURRENCY, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .subscribeSuccess {
                content = content.copy(features = content.features.copy(isCurrencyEnabled = checked))
            }
            .addTo(disposables)
    }

    fun onWeatherChecked(checked: Boolean) {
        togglesRepository.setEnabled(TogglesSet.WEATHER, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .subscribeSuccess {
                content = content.copy(features = content.features.copy(isWeatherEnabled = checked))
            }
            .addTo(disposables)
    }

    sealed interface State {
        data class Content(
            val accountInfo: Account,
            val features: Features
        ) : State

        data class Features(
            val isCameraEnabled: Boolean,
            val isAdsEnabled: Boolean,
            val isArticlesEnabled: Boolean,
            val isCurrencyEnabled: Boolean,
            val isWeatherEnabled: Boolean
        )

        object Loading : State

        object Error : State
    }
    
    class Factory @Inject constructor(
        private val togglesRepository: ITogglesRepository,
        private val accountRepository: IAccountRepository,
        private val router: AccountRouter
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AccountSettingsViewModel(togglesRepository, accountRepository, router) as T
        }
    }
}