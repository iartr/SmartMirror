package com.iartr.smartmirror.deps

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.iartr.smartmirror.camera.FaceData
import com.iartr.smartmirror.camera.FacesReceiveTask
import com.iartr.smartmirror.core.utils.dagger.AppScope
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

@AppScope
class FirebaseBasedFacesReceiveTask @Inject constructor() : FacesReceiveTask {
    private val facesDatabase = Firebase.database.reference.child("faces")

    override fun onFaceReceived(faceData: FaceData): Completable {
        return Completable.create { emitter ->
            facesDatabase.push().setValue(faceData)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }
}