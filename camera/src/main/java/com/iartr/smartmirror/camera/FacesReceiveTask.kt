package com.iartr.smartmirror.camera

import io.reactivex.rxjava3.core.Completable

lateinit var facesReceiveTaskProvider: Lazy<FacesReceiveTask>

fun interface FacesReceiveTask {
    fun onFaceReceived(faceData: FaceData): Completable
}