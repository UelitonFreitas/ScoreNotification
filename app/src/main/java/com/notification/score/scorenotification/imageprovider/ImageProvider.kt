package com.notification.score.scorenotification.imageprovider

import android.graphics.Bitmap

interface ImageProvider {
    suspend fun getImage(): Bitmap?
}