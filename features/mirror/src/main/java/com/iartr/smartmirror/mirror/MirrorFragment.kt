package com.iartr.smartmirror.mirror

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iartr.smartmirror.account.IAccountRepository
import com.iartr.smartmirror.camera.CameraController
import com.iartr.smartmirror.camera.FacesReceiveTask
import com.iartr.smartmirror.design.RetryingErrorView
import com.iartr.smartmirror.mirror.di.MirrorFeatureViewModel
import com.iartr.smartmirror.mvvm.BaseFragment
import com.iartr.smartmirror.news.News
import dagger.Lazy
import javax.inject.Inject

class MirrorFragment : BaseFragment(R.layout.fragment_mirror) {

    private lateinit var accountButton: ImageView
    private lateinit var weatherContainer: View
    private lateinit var weatherData: TextView
    private lateinit var weatherLoading: ProgressBar
    private lateinit var weatherError: RetryingErrorView

    private lateinit var currencyList: LinearLayout
    private lateinit var rubToUsd: TextView
    private lateinit var rubToEur: TextView
    private lateinit var currencyLoader: ProgressBar
    private lateinit var currencyError: RetryingErrorView

    private lateinit var articlesContainer: View
    private lateinit var articlesList: RecyclerView
    private lateinit var articlesLoader: ProgressBar
    private lateinit var articlesError: RetryingErrorView

    @Inject
    internal lateinit var articlesAdapter: Lazy<ListAdapter<News, *>>

    private val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        cameraView.isVisible = isGranted
        setupCamera()
    }

    private lateinit var cameraView: PreviewView

    private val googleAuthResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        { viewModel.onGoogleAuthResult(it.data) }
    )

    @Inject
    internal lateinit var accountRepository: Lazy<IAccountRepository>
    @Inject
    internal lateinit var facesReceiveTask: Lazy<FacesReceiveTask>
    @Inject
    internal lateinit var viewModelFactory: Lazy<MirrorViewModel.Factory>

    private val cameraController: CameraController by lazy { CameraController(facesReceiveTask.get()) }

    override val viewModel: MirrorViewModel by viewModels(
        factoryProducer = { viewModelFactory.get() }
    )

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get(MirrorFeatureViewModel::class.java)
            .mirrorComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadAll()

        accountButton = view.findViewById<ImageView>(R.id.main_account_button).apply {
            setOnClickListener { viewModel.onAccountButtonClick() }
        }
        weatherContainer = view.findViewById(R.id.main_weather_container)
        weatherData = view.findViewById(R.id.main_weather_container_data)
        weatherLoading = view.findViewById(R.id.main_weather_container_loader)
        weatherError = view.findViewById(R.id.main_weather_container_error)

        currencyList = view.findViewById(R.id.main_currency_container_list)
        rubToUsd = view.findViewById(R.id.main_rub_to_usd_currency)
        rubToEur = view.findViewById(R.id.main_rub_to_eur_currency)
        currencyLoader = view.findViewById(R.id.main_currency_container_loader)
        currencyError = view.findViewById(R.id.main_currency_container_error)

        articlesContainer = view.findViewById(R.id.main_articles_container)
        articlesList = view.findViewById(R.id.main_articles_list)
        articlesLoader = view.findViewById(R.id.main_articles_container_loader)
        articlesError = view.findViewById(R.id.main_articles_container_error)
        articlesList.adapter = articlesAdapter.get()

        cameraView = view.findViewById(R.id.main_camera_view)
        cameraView.post {
            setupCamera()
        }

        lifecycleScope.launchWhenResumed { viewModel.weatherState.collect(::applyWeatherState) }
        lifecycleScope.launchWhenResumed { viewModel.cameraState.collect(::applyCameraState) }
        lifecycleScope.launchWhenResumed { viewModel.currencyState.collect(::applyCurrencyState) }
        lifecycleScope.launchWhenResumed { viewModel.articlesState.collect(::applyArticlesState) }
        lifecycleScope.launchWhenResumed { viewModel.isAccountVisible.collect { accountButton.isVisible = it } }
        lifecycleScope.launchWhenResumed { viewModel.googleAuthSignal.collect { onGoogleAuthSignalReceive() } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraController.release()
    }

    private fun setupCamera() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                cameraController.setup(
                    lifecycleOwner = viewLifecycleOwner,
                    display = cameraView.display,
                    surfaceProvider = cameraView.surfaceProvider
                )
            }

            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED ->
                requestCamera.launch(Manifest.permission.CAMERA)

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ->
                requestCamera.launch(Manifest.permission.CAMERA)
        }
    }

    private fun applyWeatherState(weatherState: MirrorViewModel.WeatherState) =
        when (weatherState) {
            is MirrorViewModel.WeatherState.Success -> {
                weatherLoading.isVisible = false
                weatherError.hide()
                weatherData.isVisible = true
                weatherData.text = "${weatherState.icon}\n${weatherState.temperature}℃"
            }
            is MirrorViewModel.WeatherState.Error -> {
                weatherLoading.isVisible = false
                weatherError.show(retryAction = { viewModel.loadWeather() })
                weatherData.isVisible = false
            }
            is MirrorViewModel.WeatherState.Loading -> {
                weatherLoading.isVisible = true
                weatherError.hide()
                weatherData.isVisible = false
            }
            MirrorViewModel.WeatherState.Disabled -> weatherContainer.isVisible = false
        }

    private fun applyCurrencyState(currencyState: MirrorViewModel.CurrencyState) =
        when (currencyState) {
            is MirrorViewModel.CurrencyState.Success -> {
                currencyList.isVisible = true
                currencyLoader.isVisible = false
                currencyError.hide()

                rubToUsd.text =
                    getString(R.string.main_currency_usd, currencyState.exchangeRates.usdRate)
                rubToEur.text =
                    getString(R.string.main_currency_eur, currencyState.exchangeRates.eurRate)
            }
            is MirrorViewModel.CurrencyState.Loading -> {
                currencyList.isVisible = false
                currencyLoader.isVisible = true
                currencyError.hide()
            }
            is MirrorViewModel.CurrencyState.Error -> {
                currencyList.isVisible = false
                currencyLoader.isVisible = false
                currencyError.show(retryAction = { viewModel.loadCurrency() })
            }
            MirrorViewModel.CurrencyState.Disabled -> currencyList.isVisible = false
        }

    private fun applyArticlesState(articlesState: MirrorViewModel.ArticlesState) =
        when (articlesState) {
            is MirrorViewModel.ArticlesState.Error -> {
                articlesList.isVisible = false
                articlesLoader.isVisible = false
                articlesError.show(retryAction = { viewModel.loadArticles() })
            }
            is MirrorViewModel.ArticlesState.Loading -> {
                articlesList.isVisible = false
                articlesLoader.isVisible = true
                articlesError.hide()
            }
            is MirrorViewModel.ArticlesState.Success -> {
                articlesList.isVisible = true
                articlesLoader.isVisible = false
                articlesError.hide()

                articlesAdapter.get().submitList(articlesState.articles)
            }
            MirrorViewModel.ArticlesState.Disabled -> articlesContainer.isVisible = false
        }

    private fun applyCameraState(cameraState: MirrorViewModel.CameraState) = when (cameraState) {
        MirrorViewModel.CameraState.Visible -> cameraView.isVisible = true
        MirrorViewModel.CameraState.Hide -> cameraView.isVisible = false
        MirrorViewModel.CameraState.NotAvailable -> cameraView.isVisible = false
    }

    private fun onGoogleAuthSignalReceive() {
        val intent = accountRepository.get().google.getIntentForAuth()
        googleAuthResultLauncher.launch(intent)
    }
}