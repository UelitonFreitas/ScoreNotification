package com.notification.score.scorenotification.imageprocessor

import android.graphics.Bitmap

class ImageProcessorImpl: ImageProcessor {
    override suspend fun processImage(image: Bitmap): Bitmap {
        return image
    }
}