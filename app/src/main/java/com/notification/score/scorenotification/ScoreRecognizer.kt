package com.notification.score.scorenotification

import android.graphics.Bitmap
import android.os.Handler
import com.notification.score.scorenotification.classifiers.ScoreClassifier
import com.notification.score.scorenotification.imageprocessor.ImageProcessor
import com.notification.score.scorenotification.imageprovider.ImageProvider

class ScoreRecognizer(private val imageProvider: ImageProvider, private val classifier: ScoreClassifier, val onScoreChange: (String) -> Unit ) {

    private var handler = Handler()
    private val runnable = Runnable { startWatchScoreChange() }

    var imageProcessor: ImageProcessor? = null

    fun startWatchScoreChange() {
        findScore()
        handler.postDelayed(runnable, 500)
    }

    fun findScore() {
         imageProvider.getImage(::onImageCaptured)
    }

    private fun onImageCaptured(bitmap: Bitmap) {
        imageProcessor?.processImage(bitmap, ::onImageProcessed) ?: classifier.getScore(bitmap, onScoreChange)
    }

    private fun onImageProcessed(bitmap: Bitmap) {
        classifier.getScore(bitmap, onScoreChange)
    }

    fun stopWatchScoreChange() {
        handler.removeCallbacks(runnable)
    }
}