package com.notification.score.scorenotification.imageprocessor

import android.graphics.Bitmap

interface ImageProcessor {
    fun processImage(image: Bitmap, onImageProcessed: (Bitmap)->Unit)
}