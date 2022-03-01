/**
 * Copyright (c) 2020 - present, LLC “V Kontakte”
 *
 * 1. Permission is hereby granted to any person obtaining a copy of this Software to
 * use the Software without charge.
 *
 * 2. Restrictions
 * You may not modify, merge, publish, distribute, sublicense, and/or sell copies,
 * create derivative works based upon the Software or any part thereof.
 *
 * 3. Termination
 * This License is effective until terminated. LLC “V Kontakte” may terminate this
 * License at any time without any negative consequences to our rights.
 * You may terminate this License at any time by deleting the Software and all copies
 * thereof. Upon termination of this license for any reason, you shall continue to be
 * bound by the provisions of Section 2 above.
 * Termination will be without prejudice to any rights LLC “V Kontakte” may have as
 * a result of this agreement.
 *
 * 4. Disclaimer of warranty and liability
 * THE SOFTWARE IS MADE AVAILABLE ON THE “AS IS” BASIS. LLC “V KONTAKTE” DISCLAIMS
 * ALL WARRANTIES THAT THE SOFTWARE MAY BE SUITABLE OR UNSUITABLE FOR ANY SPECIFIC
 * PURPOSES OF USE. LLC “V KONTAKTE” CAN NOT GUARANTEE AND DOES NOT PROMISE ANY
 * SPECIFIC RESULTS OF USE OF THE SOFTWARE.
 * UNDER NO CIRCUMSTANCES LLC “V KONTAKTE” BEAR LIABILITY TO THE LICENSEE OR ANY
 * THIRD PARTIES FOR ANY DAMAGE IN CONNECTION WITH USE OF THE SOFTWARE.
 *//*

*/
/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*


package com.vk.camera.ui

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.vk.camera.R
import com.vk.camera.analyzer.QrAnalyzer
import com.vk.core.extensions.setVisibleOrGone
import com.vk.superapp.core.utils.WebLogger
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

internal class SuperappQrCameraFragment private constructor() : Fragment() {

    private lateinit var container: ViewGroup
    private lateinit var previewView: PreviewView

    private var displayId = -1
    private var lensFacing = CameraSelector.LENS_FACING_BACK

    private var previewUseCase: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null

    @Volatile
    private var qrDetected = false

    private lateinit var displayManager: DisplayManager
    private lateinit var callback: QrDetectedCallback
    private lateinit var workerExecutor: ExecutorService
    private lateinit var uiConfig: SuperappQrCameraUiConfig

    */
/**
     * We need a display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     *//*

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit

        @SuppressLint("UnsafeExperimentalUsageError,UnsafeOptInUsageError")
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@SuperappQrCameraFragment.displayId) {
                WebLogger.d("Rotation changed: ${view.display.rotation}")
                previewUseCase?.targetRotation = view.display.rotation
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as QrDetectedCallback
        uiConfig = arguments?.getParcelable(KEY_UI_CONFIG) as? SuperappQrCameraUiConfig ?: SuperappQrCameraUiConfig()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        workerExecutor = Executors.newSingleThreadExecutor()
        return inflater.inflate(R.layout.vk_superapp_camera_fragment, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        workerExecutor.shutdown()
        displayManager.unregisterDisplayListener(displayListener)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view as ViewGroup
        previewView = container.findViewById(R.id.camera_preview)

        // Every time the orientation of device changes, recompute layout
        displayManager = previewView.context
            .getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.registerDisplayListener(displayListener, null)

        // Wait for the views to be properly laid out
        previewView.post {
            // Keep track of the display in which this view is attached
            displayId = previewView.display.displayId

            setUpCamera()
        }
        previewView.postDelayed(
            {
                container.findViewById<View>(R.id.qr_scanner_animation).visibility = View.VISIBLE
            },
            1000
        )
        view.findViewById<View>(R.id.tv_qr_scanner_prompt).apply {
            setVisibleOrGone(uiConfig.withCaption)
        }

        view.findViewById<View>(R.id.vk_superapp_qr_camera_close).apply {
            setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            Runnable {
                try {
                    cameraProvider = cameraProviderFuture.get()

                    val hasBackCamera = hasBackCamera()
                    val hasFrontCamera = hasFrontCamera()

                    if (hasBackCamera || hasFrontCamera) {
                        lensFacing = if (hasBackCamera) {
                            CameraSelector.LENS_FACING_BACK
                        } else {
                            CameraSelector.LENS_FACING_FRONT
                        }
                        bindCameraUseCases()
                    } else {
                        callback.onCameraSetupFailed()
                    }
                } catch (e: Throwable) {
                    WebLogger.e(e)
                }
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    private fun bindCameraUseCases() {
        val metrics = DisplayMetrics().also { previewView.display.getRealMetrics(it) }
        WebLogger.d("Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        WebLogger.d("Preview aspect ratio: $screenAspectRatio")

        val rotation = previewView.display.rotation

        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

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
            .also {
                it.setAnalyzer(
                    workerExecutor,
                    QrAnalyzer(requireContext()) { data ->
                        if (!qrDetected) {
                            qrDetected = true
                            WebLogger.d("QR detected ${data.text}")
                            callback.onQrCodeDetected(data.text)
                        }
                    }
                )
            }

        cameraProvider.unbindAll()
        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, previewUseCase, imageAnalyzer
            )
            previewUseCase?.setSurfaceProvider(previewView.surfaceProvider)
        } catch (exc: Exception) {
            WebLogger.e(exc)
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        private const val ANIMATION_START_DELAY = 1000

        internal const val KEY_UI_CONFIG = "qr_ui_config"

        fun createFragment(uiConfig: SuperappQrCameraUiConfig) = SuperappQrCameraFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_UI_CONFIG, uiConfig)
            }
        }
    }
}
*/
