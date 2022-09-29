package com.iartr.smartmirror.camera

import android.annotation.SuppressLint
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.Display
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.iartr.smartmirror.core.utils.AppContextHolder
import com.iartr.smartmirror.core.utils.deviceid.DeviceIdProvider
import com.iartr.smartmirror.ext.subscribeSuccess
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraController @Inject constructor(
    private val facesReceiveTask: FacesReceiveTask
) {
    private val disposables = CompositeDisposable()

    private val deviceId: String by lazy { DeviceIdProvider.getDeviceId() }

    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    private var displayManager: DisplayManager? = null
    private var displayListener: DisplayListener? = null

    @SuppressLint("UnsafeOptInUsageError")
    fun onDisplayChanged(display: Display) {
        previewUseCase?.targetRotation = display.rotation
        imageAnalyzer?.targetRotation = display.rotation
    }

    fun setup(
        display: Display,
        surfaceProvider: Preview.SurfaceProvider,
        lifecycleOwner: LifecycleOwner
    ) {
        executor = Executors.newSingleThreadExecutor()

        displayListener = DisplayListener(display.displayId, { onDisplayChanged(display) })
        displayManager = AppContextHolder.context.getSystemService()
        displayManager?.registerDisplayListener(displayListener, null)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(AppContextHolder.context)
        cameraProviderFuture.addListener(
            Runnable {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases(display, surfaceProvider, lifecycleOwner)
            },
            ContextCompat.getMainExecutor(AppContextHolder.context)
        )
    }

    fun release() {
        executor.shutdown()
        disposables.dispose()
        displayManager?.unregisterDisplayListener(displayListener)
        displayListener = null
    }

    private fun bindCameraUseCases(
        display: Display,
        surfaceProvider: Preview.SurfaceProvider,
        lifecycleOwner: LifecycleOwner
    ) {
        val metrics = DisplayMetrics().also { display.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val rotation = display.rotation
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
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
            camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase, imageAnalyzer)
            previewUseCase?.setSurfaceProvider(surfaceProvider)
        } catch (exc: Exception) {
            android.util.Log.e("CameraController", "An error occurred on camera configured", exc)
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
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

            if (lastFaceData.trackingId != trackingId) {
                lastFaceData = lastFaceData.copy(
                    trackingId = trackingId!!,
                    deviceId = deviceId,
                    timestamp = System.currentTimeMillis(),
                    boundLeft = bounds.left,
                    boundTop = bounds.top,
                    boundRight = bounds.right,
                    boundBottom = bounds.bottom,
                    smilingProbability = smilingProbability!!,
                    leftEyeOpenProbability = leftEyeOpenProbability!!,
                    rightEyeOpenProbability = rightEyeOpenProbability!!,
                )

                facesReceiveTask.onFaceReceived(lastFaceData)
                    .subscribeSuccess { android.util.Log.d("CameraController", "face was successfully proceeded") }
                    .addTo(disposables)
            }
        }

        val facesJob: (List<Face>) -> Unit = { faces -> faces.forEach { faceJob(it) } }

        return ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                detector.process(image)
                    .addOnSuccessListener { facesJob(it) }
                    .addOnFailureListener { android.util.Log.e("FaceAnalyzer", "An error occurred on image processing", it) }
                    .addOnCompleteListener { imageProxy.close() }
            }
        }
    }

    private companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}