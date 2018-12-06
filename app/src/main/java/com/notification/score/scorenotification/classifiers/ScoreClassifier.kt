package com.notification.score.scorenotification.classifiers

import android.graphics.Bitmap

interface ScoreClassifier {
    suspend fun getScore(image: Bitmap) : String
}