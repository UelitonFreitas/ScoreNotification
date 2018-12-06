package com.notification.score.scorenotification.classifiers

import android.graphics.Bitmap

class ScoreClassifierImpl : ScoreClassifier {
    var imageSequence = 0L

    override suspend fun getScore(image: Bitmap): String {
        return "Image ${imageSequence++} processed (${image.width}/${image.height})"
    }
}