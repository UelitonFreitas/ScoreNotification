package com.notification.score.scorenotification.classifiers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions


class ScoreClassifierImpl : ScoreClassifier {
    var imageSequence = 0L

    override fun getScore(image: Bitmap, onScoreFound: (String) -> Unit, onDrawRequest: ((Bitmap)->Unit)?) {


        // High-accuracy landmark detection and face classification
        val options = FirebaseVisionFaceDetectorOptions.Builder()
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build()

        val canvas = Canvas(image)
        val fireBaseImage = FirebaseVisionImage.fromBitmap(image)
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

        Log.d("@@@@@@", "getScore")

        val result = detector.detectInImage(fireBaseImage).addOnSuccessListener { faces ->
            Log.d("@@@@@@", "Success found: ${faces.size} faces")
            for (face in faces) {
                FaceGraphic(face).draw(canvas)
                Log.d("@@@@@@", "Draw image found")
                onDrawRequest?.invoke(image)
            }
        }.addOnFailureListener {
            Log.d("@@@@@@", "Faiooooooo")
        }
    }
}