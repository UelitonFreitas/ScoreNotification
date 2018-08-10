package com.notification.score.scorenotification

import android.util.Log
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock

class OcrDetectorProcessor(val f: (String)->Unit) : Detector.Processor<TextBlock> {

    override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
        val items = detections.detectedItems
        for (i in 0 until items.size()) {
            val item = items.valueAt(i)
            if (item != null && item.value != null) {
                Log.d("OcrDetectorProcessor", "Text detected! " + item.value)
                f(item.value)
            }
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    override fun release() {
    }
}
