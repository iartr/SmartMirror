@file:Suppress("UNCHECKED_CAST")

package com.iartr.smartmirror.accountsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.iartr.smartmirror.account.Account
import com.iartr.smartmirror.account.AuthStateListener
import com.iartr.smartmirror.account.IAccountRepository
import com.iartr.smartmirror.mvvm.BaseViewModel
import com.iartr.smartmirror.toggles.ITogglesRepository
import com.iartr.smartmirror.toggles.TogglesSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.zip
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

    private val viewStateMutable = MutableStateFlow<State>(State.Loading)
    val viewState: StateFlow<State> = viewStateMutable.asStateFlow()

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
            viewStateMutable.tryEmit(withAccountInfo)
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
        val cameraSource2 = togglesRepository.isEnabled(TogglesSet.CAMERA)
        val adsSource2 = togglesRepository.isEnabled(TogglesSet.ADS)
        val articlesSource2 = togglesRepository.isEnabled(TogglesSet.ARTICLES)
        val currencySource2 = togglesRepository.isEnabled(TogglesSet.CURRENCY)
        val weatherSource2 = togglesRepository.isEnabled(TogglesSet.WEATHER)

        cameraSource2.zip(adsSource2, { t1, t2 -> State.Features(t1, t2, false, false, false) })
            .zip(articlesSource2, { state, v -> state.copy(isArticlesEnabled = v) })
            .zip(currencySource2, { state, v -> state.copy(isCurrencyEnabled = v) })
            .zip(weatherSource2, { state, v -> state.copy(isWeatherEnabled = v) })
            .onStart { viewStateMutable.emit(State.Loading) }
            .flowOn(Dispatchers.IO)
            .withErrorDisplay()
            .catch { viewStateMutable.emit(State.Error) }
            .onEach { content = content.copy(features = it) }
            .launchIn(viewModelScope)
    }

    fun onBack() = router.back()

    fun onLogoutClicked() {
        accountRepository.logout()
    }

    fun onCameraChecked(checked: Boolean) {
        togglesRepository.setEnabled(TogglesSet.CAMERA, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .onEach {
                content = content.copy(features = content.features.copy(isCameraEnabled = checked))
            }
            .launchIn(viewModelScope)
    }

    fun onAdsChecked(checked: Boolean) {
        togglesRepository.setEnabled(TogglesSet.ADS, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .onEach {
                content = content.copy(features = content.features.copy(isAdsEnabled = checked))
            }
            .launchIn(viewModelScope)
    }

    fun onArticlesChecked(checked: Boolean) {
        togglesRepository.setEnabled(TogglesSet.ARTICLES, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .onEach {
                content = content.copy(features = content.features.copy(isArticlesEnabled = checked))
            }
            .launchIn(viewModelScope)
    }

    fun onCurrencyChecked(checked: Boolean) {
        togglesRepository.setEnabled(TogglesSet.CURRENCY, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .onEach {
                content = content.copy(features = content.features.copy(isCurrencyEnabled = checked))
            }
            .launchIn(viewModelScope)
    }

    fun onWeatherChecked(checked: Boolean) {
        togglesRepository.setEnabled(TogglesSet.WEATHER, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .onEach {
                content = content.copy(features = content.features.copy(isWeatherEnabled = checked))
            }
            .launchIn(viewModelScope)
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