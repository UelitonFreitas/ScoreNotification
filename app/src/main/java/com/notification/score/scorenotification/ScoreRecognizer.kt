package com.notification.score.scorenotification

import android.graphics.Bitmap
import com.notification.score.scorenotification.classifiers.ImageClassifier
import com.notification.score.scorenotification.imageprocessor.ImageProcessor
import com.notification.score.scorenotification.imageprocessor.ImageProcessorImpl
import com.notification.score.scorenotification.imageprovider.ImageProvider
import kotlinx.coroutines.*

class ObjectRecognizer<T>(private val imageProvider: ImageProvider,
                      private val classifier: ImageClassifier<T>,
                      private val onScoreChange: (T) -> Unit,
                      private val onObjectsFoundDrown:  (Bitmap) -> Unit) {

    var imageProcessor: ImageProcessor? = ImageProcessorImpl()
    var onImageProcessed: ((bitmap: Bitmap) -> Unit)? = null
    var job: Job? = null

    fun startWatchScoreChange() {
        job = GlobalScope.launch {
            while(isActive) {
                findScore()
                delay(1000L)
            }
        }
    }

    fun findScore() {
         imageProvider.getImage(::onImageCaptured)
    }

    private fun onImageCaptured(bitmap: Bitmap) {
        imageProcessor?.processImage(bitmap, ::onImageProcessed) ?: classifier.getScore(bitmap, onScoreChange, onObjectsFoundDrown)
    }

    private fun onImageProcessed(bitmap: Bitmap) {
        onImageProcessed?.invoke(bitmap)
        classifier.getScore(bitmap, onScoreChange, onObjectsFoundDrown)
    }

    fun stopWatchScoreChange() {
        GlobalScope.launch {
            job?.cancelAndJoin()
        }
    }
}