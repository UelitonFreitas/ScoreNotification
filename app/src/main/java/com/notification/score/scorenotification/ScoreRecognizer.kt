package com.notification.score.scorenotification

import android.graphics.Bitmap
import com.notification.score.scorenotification.classifiers.ScoreClassifier
import com.notification.score.scorenotification.imageprocessor.ImageProcessor
import com.notification.score.scorenotification.imageprocessor.ImageProcessorImpl
import com.notification.score.scorenotification.imageprovider.ImageProvider
import kotlinx.coroutines.*

class ScoreRecognizer(private val imageProvider: ImageProvider,
                      private val classifier: ScoreClassifier,
                      private val onScoreChange: (String) -> Unit) {

    var imageProcessor: ImageProcessor? = ImageProcessorImpl()
    var onImageProcessed: ((bitmap: Bitmap) -> Unit)? = null
    var job: Job? = null

    fun startWatchScoreChange() {
        job = GlobalScope.launch {
            while(isActive) {
                findScore()
                delay(500L)
            }
        }
    }

    fun findScore() {
         imageProvider.getImage(::onImageCaptured)
    }

    private fun onImageCaptured(bitmap: Bitmap) {
        imageProcessor?.processImage(bitmap, ::onImageProcessed) ?: classifier.getScore(bitmap, onScoreChange)
    }

    private fun onImageProcessed(bitmap: Bitmap) {
        onImageProcessed?.invoke(bitmap)
        classifier.getScore(bitmap, onScoreChange)
    }

    fun stopWatchScoreChange() {
        GlobalScope.launch {
            job?.cancelAndJoin()
        }
    }
}