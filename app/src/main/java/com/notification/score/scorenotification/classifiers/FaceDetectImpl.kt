package com.notification.score.scorenotification.classifiers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.google.android.gms.vision.face.Face
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions


class FaceDetectImpl : ImageClassifier<List<FirebaseVisionFace>> {

    val tag = "FaceDetectImpl"

    override fun getScore(image: Bitmap, onDetect: (List<FirebaseVisionFace>) -> Unit, onDrawRequest: ((Bitmap)->Unit)?) {

        // High-accuracy landmark detection and face classification
        val options = FirebaseVisionFaceDetectorOptions.Builder()
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build()

        val canvas = Canvas(image)
        val fireBaseImage = FirebaseVisionImage.fromBitmap(image)
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

        val result = detector.detectInImage(fireBaseImage).addOnSuccessListener { faces ->
            Log.d(tag, "Success found: ${faces.size} faces")
            for (face in faces) {
                FaceGraphic(face).draw(canvas)
                Log.d(tag, "Draw image found")
                onDrawRequest?.invoke(image)
                onDetect(faces)
            }
        }.addOnFailureListener {
            Log.d(tag, "Faiooooooo")
        }
    }
}