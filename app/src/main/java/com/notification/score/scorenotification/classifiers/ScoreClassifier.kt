package com.notification.score.scorenotification.classifiers

import android.graphics.Bitmap

interface ScoreClassifier {
    fun getScore(image: Bitmap, onScoreFound: (String)->Unit)
}