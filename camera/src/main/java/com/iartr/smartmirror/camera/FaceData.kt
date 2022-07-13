package com.iartr.smartmirror.camera

data class FaceData(
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