package com.iartr.smartmirror.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import com.iartr.smartmirror.R
import com.iartr.smartmirror.data.articles.ArticlesRepository
import com.iartr.smartmirror.data.articles.newsApi
import com.iartr.smartmirror.data.coord.CoordRepository
import com.iartr.smartmirror.data.coord.coordApi
import com.iartr.smartmirror.data.currency.CurrencyRepository
import com.iartr.smartmirror.data.currency.currencyApi
import com.iartr.smartmirror.data.weather.WeatherRepository
import com.iartr.smartmirror.data.weather.weatherApi
import com.iartr.smartmirror.ui.base.BaseFragment
import com.iartr.smartmirror.ui.main.articles.ArticlesAdapter
import com.iartr.smartmirror.utils.RetryingErrorView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MainFragment : BaseFragment(R.layout.fragment_main) {
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

    private lateinit var articlesList: RecyclerView
    private lateinit var articlesLoader: ProgressBar
    private lateinit var articlesError: RetryingErrorView
    private val articlesAdapter: ArticlesAdapter by lazy { ArticlesAdapter() }

    private lateinit var adView: AdView

    private val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        cameraView.isVisible = isGranted
        if (isGranted) {
            setupCamera()
        }
    }
    private lateinit var cameraView: PreviewView
    private lateinit var executor: ExecutorService
    private lateinit var displayManager: DisplayManager
    private var displayId = -1
    private var previewUseCase: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    /**
     * We need a display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit

        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@MainFragment.displayId) {
                previewUseCase?.targetRotation = view.display.rotation
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    private val viewModel: MainViewModel by viewModels(
        factoryProducer = {
            MainViewModel.Factory(
                weatherRepository = WeatherRepository(
                    api = weatherApi,
                    coordRepository = CoordRepository(coordApi = coordApi)
                ),
                currencyRepository = CurrencyRepository(api = currencyApi),
                articlesRepository = ArticlesRepository(api = newsApi)
            )
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        executor = Executors.newSingleThreadExecutor()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountButton = view.findViewById(R.id.main_account_button)
        weatherContainer = view.findViewById(R.id.main_weather_container)
        weatherData = view.findViewById(R.id.main_weather_container_data)
        weatherLoading = view.findViewById(R.id.main_weather_container_loader)
        weatherError = view.findViewById(R.id.main_weather_container_error)
        currencyList = view.findViewById(R.id.main_currency_container_list)
        rubToUsd = view.findViewById(R.id.main_rub_to_usd_currency)
        rubToEur = view.findViewById(R.id.main_rub_to_eur_currency)
        currencyLoader = view.findViewById(R.id.main_currency_container_loader)
        currencyError = view.findViewById(R.id.main_currency_container_error)

        articlesList = view.findViewById(R.id.main_articles_list)
        articlesLoader = view.findViewById(R.id.main_articles_container_loader)
        articlesError = view.findViewById(R.id.main_articles_container_error)
        articlesList.adapter = articlesAdapter

        cameraView = view.findViewById(R.id.main_camera_view)
        displayManager = cameraView.context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.registerDisplayListener(displayListener, null)
        cameraView.post {
            displayId = cameraView.display.displayId
            setupCamera()
        }

        adView = view.findViewById<AdView>(R.id.main_ad_view).apply {
            resume()
            loadAd(viewModel.getAdRequest())
            adListener = viewModel.adListener
        }

        viewModel.weatherState.subscribeWithFragment(::applyWeatherState)
        viewModel.currencyState.subscribeWithFragment(::applyCurrencyState)
        viewModel.articlesState.subscribeWithFragment(::applyArticlesState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adView.pause()
        executor.shutdown()
        displayManager.unregisterDisplayListener(displayListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    private fun setupCamera() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
                cameraProviderFuture.addListener(
                    Runnable {
                        cameraProvider = cameraProviderFuture.get()
                        lensFacing = CameraSelector.LENS_FACING_FRONT
                        bindCameraUseCases()
                    },
                    ContextCompat.getMainExecutor(requireContext())
                )
            }
            else -> requestCamera.launch(Manifest.permission.CAMERA)
        }
    }

    private fun bindCameraUseCases() {
        val metrics = DisplayMetrics().also { cameraView.display.getRealMetrics(it) }

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

        val rotation = cameraView.display.rotation
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        previewUseCase = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        val analyzer = object : ImageAnalysis.Analyzer {
            override fun analyze(image: ImageProxy) {
                //
            }
        }
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(
                    executor,
                    analyzer
                )
            }

        cameraProvider.unbindAll()
        try {
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase, imageAnalyzer)
            previewUseCase?.setSurfaceProvider(cameraView.surfaceProvider)
        } catch (exc: Exception) {
            android.util.Log.e("MainFragment", "Camera error", exc)
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    private fun applyWeatherState(weatherState: MainViewModel.WeatherState) =
        when (weatherState) {
            is MainViewModel.WeatherState.Success -> {
                weatherLoading.isVisible = false
                weatherError.hide()
                weatherData.isVisible = true
                weatherData.text = "${weatherState.icon}\n${weatherState.temperature}℃"
            }
            is MainViewModel.WeatherState.Error -> {
                weatherLoading.isVisible = false
                weatherError.show(retryAction = { viewModel.loadWeather() })
                weatherData.isVisible = false
            }
            is MainViewModel.WeatherState.Loading -> {
                weatherLoading.isVisible = true
                weatherError.hide()
                weatherData.isVisible = false
            }
        }

    private fun applyCurrencyState(currencyState: MainViewModel.CurrencyState) {
        when (currencyState) {
            is MainViewModel.CurrencyState.Success -> {
                currencyList.isVisible = true
                currencyLoader.isVisible = false
                currencyError.hide()

                rubToUsd.text = getString(R.string.main_currency_usd, currencyState.exchangeRates.usdRate)
                rubToEur.text = getString(R.string.main_currency_eur, currencyState.exchangeRates.eurRate)
            }
            is MainViewModel.CurrencyState.Loading -> {
                currencyList.isVisible = false
                currencyLoader.isVisible = true
                currencyError.hide()
            }
            is MainViewModel.CurrencyState.Error -> {
                currencyList.isVisible = false
                currencyLoader.isVisible = false
                currencyError.show(retryAction = { viewModel.loadCurrency() })
            }
        }
    }

    private fun applyArticlesState(articlesState: MainViewModel.ArticlesState) {
        when (articlesState) {
            is MainViewModel.ArticlesState.Error -> {
                articlesList.isVisible = false
                articlesLoader.isVisible = false
                articlesError.show(retryAction = { viewModel.loadArticles() })
            }
            is MainViewModel.ArticlesState.Loading -> {
                articlesList.isVisible = false
                articlesLoader.isVisible = true
                articlesError.hide()
            }
            is MainViewModel.ArticlesState.Success -> {
                articlesList.isVisible = true
                articlesLoader.isVisible = false
                articlesError.hide()

                articlesAdapter.submitList(articlesState.articles)
            }
        }
    }

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}