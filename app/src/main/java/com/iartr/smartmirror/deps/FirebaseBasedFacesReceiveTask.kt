package com.iartr.smartmirror.deps

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.iartr.smartmirror.camera.FaceData
import com.iartr.smartmirror.camera.FacesReceiveTask
import com.iartr.smartmirror.core.utils.dagger.AppScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@AppScope
class FirebaseBasedFacesReceiveTask @Inject constructor() : FacesReceiveTask {
    private val facesDatabase = Firebase.database.reference.child("faces")

    override fun onFaceReceived(faceData: FaceData): Flow<Unit> {
        return callbackFlow {
            facesDatabase.push().setValue(faceData)
                .addOnSuccessListener {
                    trySend(Unit)
//                    trySendBlocking(Unit) ? error handling? why not just flow + emit?
                    close()
                }
                .addOnFailureListener { close(it) }

            awaitClose {  }
        }
    }
}