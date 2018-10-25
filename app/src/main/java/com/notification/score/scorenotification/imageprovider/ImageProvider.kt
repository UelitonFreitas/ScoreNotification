package com.notification.score.scorenotification.imageprovider

import android.graphics.Bitmap

interface ImageProvider {
    fun getImage(onImageCaptured: (Bitmap) -> Unit)
}