package com.iartr.smartmirror.camera

import kotlinx.coroutines.flow.Flow

fun interface FacesReceiveTask {
    fun onFaceReceived(faceData: FaceData): Flow<Unit>
}