package com.iartr.smartmirror.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.iartr.smartmirror.R
import com.iartr.smartmirror.deviceid.DeviceIdProvider
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

    private lateinit var articlesContainer: View
    private lateinit var articlesList: RecyclerView
    private lateinit var articlesLoader: ProgressBar
    private lateinit var articlesError: RetryingErrorView
    private val articlesAdapter: ArticlesAdapter by lazy { ArticlesAdapter() }

    private lateinit var adView: AdView

    private val requestCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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

        @SuppressLint("UnsafeOptInUsageError")
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@MainFragment.displayId) {
                previewUseCase?.targetRotation = view.display.rotation
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    private val googleAuthResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        { viewModel.onGoogleAuthResult(it.data) }
    )

    private val facesDatabase = Firebase.database.reference.child("faces")
    override val viewModel: MainViewModel by viewModels()

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

        viewModel.loadAll()

        accountButton = view.findViewById<ImageView>(R.id.main_account_button).apply {
            setOnClickListener { viewModel.onAccountButtonClick() }
            setOnLongClickListener {
                viewModel.onAccountButtonLongClickListener()
                true
            }
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
        articlesList.adapter = articlesAdapter

        cameraView = view.findViewById(R.id.main_camera_view)
        displayManager =
            cameraView.context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
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

        viewModel.isAdVisible.subscribeWithFragment { adView.isVisible = it }
        viewModel.isAccountVisible.subscribeWithFragment { accountButton.isVisible = it }
        viewModel.weatherState.subscribeWithFragment(::applyWeatherState)
        viewModel.currencyState.subscribeWithFragment(::applyCurrencyState)
        viewModel.articlesState.subscribeWithFragment(::applyArticlesState)
        viewModel.cameraState.subscribeWithFragment(::applyCameraState)
        viewModel.googleAuthSignal.subscribeWithFragment { onGoogleAuthSignalReceive() }
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
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
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

            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED ->
                requestCamera.launch(Manifest.permission.CAMERA)

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ->
                requestCamera.launch(Manifest.permission.CAMERA)
        }
    }

    private fun bindCameraUseCases() {
        val metrics = DisplayMetrics().also { cameraView.display.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val rotation = cameraView.display.rotation
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        previewUseCase = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply { setAnalyzer(executor, faceAnalyzer()) }
        cameraProvider.unbindAll()
        try {
            camera =
                cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase, imageAnalyzer)
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
            MainViewModel.WeatherState.Disabled -> weatherContainer.isVisible = false
        }

    private fun applyCurrencyState(currencyState: MainViewModel.CurrencyState) =
        when (currencyState) {
            is MainViewModel.CurrencyState.Success -> {
                currencyList.isVisible = true
                currencyLoader.isVisible = false
                currencyError.hide()

                rubToUsd.text =
                    getString(R.string.main_currency_usd, currencyState.exchangeRates.usdRate)
                rubToEur.text =
                    getString(R.string.main_currency_eur, currencyState.exchangeRates.eurRate)
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
            MainViewModel.CurrencyState.Disabled -> currencyList.isVisible = false
        }

    private fun applyArticlesState(articlesState: MainViewModel.ArticlesState) =
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
            MainViewModel.ArticlesState.Disabled -> articlesContainer.isVisible = false
        }

    private fun applyCameraState(cameraState: MainViewModel.CameraState) = when (cameraState) {
        MainViewModel.CameraState.Visible -> cameraView.isVisible = true
        MainViewModel.CameraState.Hide -> cameraView.isVisible = false
        MainViewModel.CameraState.NotAvailable -> cameraView.isVisible = false
    }

    private fun onGoogleAuthSignalReceive() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id_google))
            .requestEmail()
            .build()

        val intent = GoogleSignIn.getClient(requireContext(), googleSignInOptions).signInIntent
        googleAuthResultLauncher.launch(intent)
    }

    private data class FaceData(
        val trackingId: Int,
        val deviceId: String,
        val timestamp: Long,

        val boundLeft: Int,
        val boundTop: Int,
        val boundRight: Int,
        val boundBottom: Int,

        val smilingProbability: Float,
        val leftEyeOpenProbability: Float,
        val rightEyeOpenProbability: Float,
    ) {
        companion object {
            val EMPTY = FaceData(
                trackingId = -1,
                deviceId = "",
                timestamp = -1,
                boundLeft = -1,
                boundTop = -1,
                boundRight = -1,
                boundBottom = -1,
                smilingProbability = -1f,
                leftEyeOpenProbability = -1f,
                rightEyeOpenProbability = -1f,
            )
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun faceAnalyzer(): ImageAnalysis.Analyzer {
        val highAccuracyOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setMinFaceSize(0.1f)
            .enableTracking()
            .build()

        val detector = FaceDetection.getClient(highAccuracyOptions)

        // Single thread executor so concurrency is not needed
        var lastFaceData = FaceData.EMPTY

        val faceJob: (Face) -> Unit = { face ->
            val bounds: Rect = face.boundingBox
            val rotX: Float = face.headEulerAngleX
            val rotY: Float = face.headEulerAngleY // Head is rotated to the right rotY degrees
            val rotZ: Float = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

            // if landmark was enabled: mouth, eyes, ears, cheeks, nose
            val landmarks = face.allLandmarks

            // If contour detection was enabled:
            val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
            val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

            // If classification was enabled:
            val smilingProbability = face.smilingProbability
            val leftEyeOpenProbability = face.leftEyeOpenProbability
            val rightEyeOpenProbability = face.rightEyeOpenProbability

            // If face tracking was enabled:
            val trackingId = face.trackingId

            android.util.Log.d(
                "FaceAnalyzer", """
                Face was detected!
                face ID: $trackingId
                bounds: $bounds
                rotx: $rotX, roty: $rotY, rotz: $rotZ
                landmarks: $landmarks
                smiling: $smilingProbability
                leftEye: $leftEyeOpenProbability
                rightEye: $rightEyeOpenProbability
            """.trimIndent()
            )

            if (lastFaceData.trackingId != trackingId) {
                lastFaceData = lastFaceData.copy(
                    trackingId = trackingId!!,
                    deviceId = DeviceIdProvider.getDeviceId(),
                    timestamp = System.currentTimeMillis(),
                    boundLeft = bounds.left,
                    boundTop = bounds.top,
                    boundRight = bounds.right,
                    boundBottom = bounds.bottom,
                    smilingProbability = smilingProbability!!,
                    leftEyeOpenProbability = leftEyeOpenProbability!!,
                    rightEyeOpenProbability = rightEyeOpenProbability!!,
                )
                facesDatabase.push().setValue(lastFaceData)
                android.util.Log.d("FaceAnalyzer", "Value was sent to database $lastFaceData")
            }
        }

        val facesJob: (List<Face>) -> Unit = { faces -> faces.forEach { faceJob(it) } }

        return ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                detector.process(image)
                    .addOnSuccessListener { facesJob(it) }
                    .addOnFailureListener {
                        android.util.Log.e(
                            "FaceAnalyzer",
                            "failure listener",
                            it
                        )
                    }
                    .addOnCompleteListener { imageProxy.close() }
            }
        }
    }

    private companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}