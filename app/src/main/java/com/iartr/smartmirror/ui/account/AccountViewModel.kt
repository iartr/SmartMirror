package com.iartr.smartmirror.ui.account

import com.google.firebase.auth.FirebaseAuth
import com.iartr.smartmirror.R
import com.iartr.smartmirror.data.account.Account
import com.iartr.smartmirror.ui.base.BaseViewModel
import com.iartr.smartmirror.utils.subscribeSuccess
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

class AccountViewModel : BaseViewModel() {
    private val authListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser == null) {
            router.showToast(R.string.you_was_sign_out)
            router.back()
            return@AuthStateListener
        }
    }

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
            isCameraEnabled = FeaturesRepository.FeatureSet.CAMERA.defaultEnabled,
            isAdsEnabled = FeaturesRepository.FeatureSet.ADS.defaultEnabled,
            isArticlesEnabled = FeaturesRepository.FeatureSet.ARTICLES.defaultEnabled,
            isCurrencyEnabled = FeaturesRepository.FeatureSet.CURRENCY.defaultEnabled,
            isWeatherEnabled = FeaturesRepository.FeatureSet.WEATHER.defaultEnabled,
        )
    )
        set(value) {
            val withAccountInfo = accountRepository.getAccountInfo()?.let { value.copy(accountInfo = it) } ?: value
            field = withAccountInfo
            viewStateMutable.onNext(withAccountInfo)
        }

    private val viewStateMutable: BehaviorSubject<State> = BehaviorSubject.createDefault(State.Loading)
    val viewState: Observable<State> = viewStateMutable.distinctUntilChanged()

    // DI
    private val accountRepository: AccountRepository = AccountRepository()
    override val router: AccountRouter = AccountRouter()
    private val featureRepository: FeaturesRepository = FeaturesRepository()

    init {
        accountRepository.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        super.onCleared()
        accountRepository.removeAuthStateListener(authListener)
    }

    fun loadFeatures() {
        val cameraSource = featureRepository.isEnabled(FeaturesRepository.FeatureSet.CAMERA)
        val adsSource = featureRepository.isEnabled(FeaturesRepository.FeatureSet.ADS)
        val articlesSource = featureRepository.isEnabled(FeaturesRepository.FeatureSet.ARTICLES)
        val currencySource = featureRepository.isEnabled(FeaturesRepository.FeatureSet.CURRENCY)
        val weatherSource = featureRepository.isEnabled(FeaturesRepository.FeatureSet.WEATHER)
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
        featureRepository.setEnabled(FeaturesRepository.FeatureSet.CAMERA, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .subscribeSuccess {
                content = content.copy(features = content.features.copy(isCameraEnabled = checked))
            }
            .addTo(disposables)
    }

    fun onAdsChecked(checked: Boolean) {
        featureRepository.setEnabled(FeaturesRepository.FeatureSet.ADS, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .subscribeSuccess {
                content = content.copy(features = content.features.copy(isAdsEnabled = checked))
            }
            .addTo(disposables)
    }

    fun onArticlesChecked(checked: Boolean) {
        featureRepository.setEnabled(FeaturesRepository.FeatureSet.ARTICLES, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .subscribeSuccess {
                content = content.copy(features = content.features.copy(isArticlesEnabled = checked))
            }
            .addTo(disposables)
    }

    fun onCurrencyChecked(checked: Boolean) {
        featureRepository.setEnabled(FeaturesRepository.FeatureSet.CURRENCY, checked)
            .withProgressDialog()
            .withErrorDisplay()
            .subscribeSuccess {
                content = content.copy(features = content.features.copy(isCurrencyEnabled = checked))
            }
            .addTo(disposables)
    }

    fun onWeatherChecked(checked: Boolean) {
        featureRepository.setEnabled(FeaturesRepository.FeatureSet.WEATHER, checked)
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
}

