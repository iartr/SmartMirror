package com.iartr.smartmirror.camera

import io.reactivex.rxjava3.core.Completable

fun interface FacesReceiveTask {
    fun onFaceReceived(faceData: FaceData): Completable
}