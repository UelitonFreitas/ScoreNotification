package com.notification.score.scorenotification.imageprocessor

import android.graphics.Bitmap

interface ImageProcessor {
    suspend fun processImage(image: Bitmap): Bitmap
}