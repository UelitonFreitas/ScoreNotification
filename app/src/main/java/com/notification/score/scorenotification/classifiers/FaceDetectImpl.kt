package com.notification.score.scorenotification.classifiers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions


class FaceDetectImpl : ImageClassifier<List<FirebaseVisionFace>> {

    val tag = "FaceDetectImpl"

    override fun getScore(image: Bitmap, onDetect: (List<FirebaseVisionFace>) -> Unit, onObjectsFoundDrown: ((Bitmap)->Unit)?) {

        // High-accuracy landmark detection and face classification
        val options = FirebaseVisionFaceDetectorOptions.Builder()
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build()

        with(FirebaseVision.getInstance().getVisionFaceDetector(options)) {

            detectInImage(FirebaseVisionImage.fromBitmap(image)).addOnSuccessListener { faces ->
                Log.d(tag, "Success found: ${faces.size} faces")
                for (face in faces) {
                    Log.d(tag, "Draw image found")
                    onDetect(faces)
                    drawObjectsFound(face, image, onObjectsFoundDrown)
                }
            }.addOnFailureListener {
                Log.d(tag, "Faiooooooo")
            }
        }
    }

    private fun drawObjectsFound(face: FirebaseVisionFace?, image: Bitmap, onObjectsFoundDrown: ((Bitmap) -> Unit)?) {
        FaceGraphic(face).draw(Canvas(image))
        onObjectsFoundDrown?.invoke(image)
    }
}