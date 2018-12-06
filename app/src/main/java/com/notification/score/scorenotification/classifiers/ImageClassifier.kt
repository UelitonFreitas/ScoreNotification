package com.notification.score.scorenotification.classifiers

import android.graphics.Bitmap

interface ImageClassifier<T> {
    fun getScore(image: Bitmap, onDetect: (T)->Unit, onObjectsFoundDrown: ((Bitmap)->Unit)? = null)
}