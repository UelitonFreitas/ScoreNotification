package com.notification.score.scorenotification.classifiers

import android.graphics.Bitmap

class ScoreClassifierImpl() : ScoreClassifier {
    var imageSequence = 0L

    override fun getScore(image: Bitmap, onScoreFound: (String)->Unit) {
        onScoreFound.invoke("Image ${imageSequence++} processed (${image.width}/${image.height})")
    }
}