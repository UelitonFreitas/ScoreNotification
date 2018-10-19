package com.notification.score.scorenotification.classifiers

import android.content.Context
import android.graphics.Bitmap
import com.notification.score.scorenotification.TessOCR

class ScoreClassifierImpl(context: Context, val onScoreFound: (String) -> Unit) : ScoreClassifier {

    var ocr: TessOCR = TessOCR(context) { onScoreFound(it) }

    override fun getScore(image: Bitmap) {
            ocr.processOcr(image)
    }

    fun start(){ ocr.initalizeTess() }
}