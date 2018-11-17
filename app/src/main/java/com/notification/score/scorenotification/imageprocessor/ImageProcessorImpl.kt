package com.notification.score.scorenotification.imageprocessor

import android.graphics.Bitmap

class ImageProcessorImpl: ImageProcessor {
    override fun processImage(image: Bitmap, onImageProcessed: (Bitmap) -> Unit) {
        onImageProcessed(image)
    }
}