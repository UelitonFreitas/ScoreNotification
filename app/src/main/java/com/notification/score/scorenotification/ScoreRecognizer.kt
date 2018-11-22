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

    var imageProcessor = ImageProcessorImpl()
    var onImageProcessed: ((bitmap: Bitmap) -> Unit)? = null
    var job: Job? = null

    fun startWatchScoreChange() {
        job = GlobalScope.launch {
            while(isActive) {
                imageProvider.getImage()?.let { findScore(it) }
                delay(500L)
            }
        }
    }

    private fun findScore(image: Bitmap) {
        val processedImage = imageProcessor.processImage(image)
        onImageProcessed?.invoke(processedImage)

        val score = classifier.getScore(processedImage)
        onScoreChange(score)
    }

    fun stopWatchScoreChange() {
        GlobalScope.launch {
            job?.cancelAndJoin()
        }
    }
}