package com.notification.score.scorenotification.classifiers

import android.content.Context
import android.graphics.Bitmap
import com.notification.score.scorenotification.TessOCR

class ScoreClassifierImpl(context: Context, var onScoreFound: ((String) -> Unit)? = null) : ScoreClassifier {

    var ocr: TessOCR = TessOCR(context) { onScoreFound?.invoke(it) }

    override fun getScore(image: Bitmap, newOnScoreFound: (String)->Unit) {
        onScoreFound = newOnScoreFound
        ocr.processOcr(image)
    }

    fun start(){
        ocr.initalizeTess()
    }
}