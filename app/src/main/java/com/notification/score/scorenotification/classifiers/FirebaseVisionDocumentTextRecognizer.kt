package com.notification.score.scorenotification.classifiers

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage


class FirebaseVisionDocumentTextRecognizer(private val context: Context): ScoreClassifier {

    fun start(){
        FirebaseApp.initializeApp(context)
    }

    override fun getScore(image: Bitmap, onScoreFound: (String) -> Unit) {

        val firebaseImage = FirebaseVisionImage.fromBitmap(image)

        val textRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
        textRecognizer.processImage(firebaseImage).addOnSuccessListener { result ->
            val resultText = result.text
            for (block in result.textBlocks) {
                val blockText = block.getText()
                val blockConfidence = block.getConfidence()
                val blockLanguages = block.getRecognizedLanguages()
                val blockCornerPoints = block.getCornerPoints()
                val blockFrame = block.getBoundingBox()
                for (line in block.getLines()) {
                    val lineText = line.getText()
                    val lineConfidence = line.getConfidence()
                    val lineLanguages = line.getRecognizedLanguages()
                    val lineCornerPoints = line.getCornerPoints()
                    val lineFrame = line.getBoundingBox()
                    for (element in line.getElements()) {
                        val elementText = element.getText()
                        val elementConfidence = element.getConfidence()
                        val elementLanguages = element.getRecognizedLanguages()
                        val elementCornerPoints = element.getCornerPoints()
                        val elementFrame = element.getBoundingBox()
                    }
                }
            }
            onScoreFound(resultText)
        }

    }
}