package com.iartr.smartmirror.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.iartr.smartmirror.R
import com.iartr.smartmirror.data.articles.ArticlesRepository
import com.iartr.smartmirror.data.articles.newsApi
import com.iartr.smartmirror.data.coord.CoordRepository
import com.iartr.smartmirror.data.coord.coordApi
import com.iartr.smartmirror.data.currency.CurrencyRepository
import com.iartr.smartmirror.data.currency.currencyApi
import com.iartr.smartmirror.data.weather.WeatherRepository
import com.iartr.smartmirror.data.weather.weatherApi
import com.iartr.smartmirror.deviceid.DeviceIdProvider
import com.iartr.smartmirror.toggles.*
import com.iartr.smartmirror.ui.account.AccountFragment
import com.iartr.smartmirror.ui.debug.PreferenceActivity
import com.iartr.smartmirror.ui.base.BaseFragment
import com.iartr.smartmirror.ui.main.articles.ArticlesAdapter
import com.iartr.smartmirror.utils.RetryingErrorView
import com.iartr.smartmirror.utils.subscribeSuccess
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

        @SuppressLint("UnsafeOptInUsageError")
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@MainFragment.displayId) {
                previewUseCase?.targetRotation = view.display.rotation
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    // TODO: auth controller
    private val firebaseAuth: FirebaseAuth = Firebase.auth
    private val firebaseAuthStateListener: FirebaseAuth.AuthStateListener = object : FirebaseAuth.AuthStateListener {
        override fun onAuthStateChanged(auth: FirebaseAuth) {
            // Right after the listener has been registered
            // When a user is signed in
            // When the current user is signed out
            // When the current user changes


        }
    }
    private val googleAuthResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        GoogleSignIn.getSignedInAccountFromIntent(it.data)
            .addOnSuccessListener(requireActivity()) { googleAccount ->
                android.util.Log.d("GoogleAuthController", "Success: $googleAccount")
                val googleCredentials = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
                firebaseAuth.signInWithCredential(googleCredentials)
                    .addOnSuccessListener(requireActivity()) {
                        it.additionalUserInfo
                        it.credential
                        it.user

                        // TODO: router
                        openAccount()
                    }
                    .addOnFailureListener(requireActivity()) { android.util.Log.e("GoogleAuthController", "Failed", it) }
                    .addOnCompleteListener(requireActivity()) { android.util.Log.d("GoogleAuthController", "Task completed") }
            }
            .addOnFailureListener(requireActivity()) { android.util.Log.e("GoogleAuthController", "Failed", it) }
            .addOnCompleteListener(requireActivity()) { android.util.Log.d("GoogleAuthController", "Task completed") }
    }

    private val remoteConfig = Firebase.remoteConfig
    // TODO: to repository + RxJava. Move camera to CameraController
    private val database = Firebase.database
    private val facesDatabase = database.reference.child("faces")
    private val userDatabase = database.reference.child("${firebaseAuth.uid}")
    private val viewModel: MainViewModel by viewModels(
        factoryProducer = {
            MainViewModel.Factory(
                weatherRepository = WeatherRepository(
                    api = weatherApi,
                    coordRepository = CoordRepository(coordApi = coordApi)
                ),
                currencyRepository = CurrencyRepository(api = currencyApi),
                articlesRepository = ArticlesRepository(api = newsApi),
                cameraFeatureToggle = CameraFeatureToggle(requireContext(), remoteConfig, userDatabase),
                accountFeatureToggle = AccountFeatureToggle(remoteConfig, userDatabase),
                adsFeatureToggle = AdsFeatureToggle(remoteConfig, userDatabase),
                articlesFeatureToggle = ArticlesFeatureToggle(remoteConfig, userDatabase),
                currencyFeatureToggle = CurrencyFeatureToggle(remoteConfig, userDatabase),
                weatherFeatureToggle = WeatherFeatureToggle(remoteConfig, userDatabase),
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

        accountButton = view.findViewById<ImageView>(R.id.main_account_button).apply {
            setOnClickListener {
                if (firebaseAuth.currentUser != null) {
                    openAccount()
                    return@setOnClickListener
                }
                val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("666377868857-6b3mldm7i4d3ppvv5u865aufhc90suu2.apps.googleusercontent.com")
                    .requestEmail()
                    .build()

                val googleClient = GoogleSignIn.getClient(context, googleSignInOptions)
                val googleIntent = googleClient.signInIntent
                googleAuthResultLauncher.launch(googleIntent)
            }
            setOnLongClickListener {
                viewModel.onAccountButtonLongClickListener()
                startActivity(Intent(context, PreferenceActivity::class.java)) // TODO: router
                true
            }
        }
        AccountFeatureToggle(remoteConfig, userDatabase).isActive()
            .subscribeSuccess { accountButton.isVisible = it }
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
        AdsFeatureToggle(remoteConfig, userDatabase).isActive()
            .subscribeSuccess { adView.isVisible = it }

        viewModel.weatherState.subscribeWithFragment(::applyWeatherState)
        viewModel.currencyState.subscribeWithFragment(::applyCurrencyState)
        viewModel.articlesState.subscribeWithFragment(::applyArticlesState)
        viewModel.cameraState.subscribeWithFragment(::applyCameraState)
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

            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ->
                requestCamera.launch(Manifest.permission.CAMERA)

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ->
                requestCamera.launch(Manifest.permission.CAMERA)
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
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply { setAnalyzer(executor, faceAnalyzer()) }
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
            MainViewModel.WeatherState.Disabled -> weatherContainer.isVisible = false
        }

    private fun applyCurrencyState(currencyState: MainViewModel.CurrencyState) = when (currencyState) {
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
        MainViewModel.CurrencyState.Disabled -> currencyList.isVisible = false
    }

    private fun applyArticlesState(articlesState: MainViewModel.ArticlesState) = when (articlesState) {
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

            android.util.Log.d("FaceAnalyzer", """
                Face was detected!
                face ID: $trackingId
                bounds: $bounds
                rotx: $rotX, roty: $rotY, rotz: $rotZ
                landmarks: $landmarks
                smiling: $smilingProbability
                leftEye: $leftEyeOpenProbability
                rightEye: $rightEyeOpenProbability
            """.trimIndent())

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
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                detector.process(image)
                    .addOnSuccessListener { facesJob(it) }
                    .addOnFailureListener { android.util.Log.e("FaceAnalyzer", "failure listener", it) }
                    .addOnCompleteListener { imageProxy.close() }
            }
        }
    }

    private fun openAccount() {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.addToBackStack(null)
            ?.replace(R.id.fragment_container_view, AccountFragment.newInstance())
            ?.commit()
    }

    private companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}