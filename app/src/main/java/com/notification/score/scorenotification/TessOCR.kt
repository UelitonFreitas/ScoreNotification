package com.notification.score.scorenotification

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer


class TessOCR(val context: Context, val language: String = "eng", val f: (String)->Unit) {

    companion object {
        const val tag = "TessOCR"
    }

    val basePath = Environment.getExternalStorageDirectory().toString() + "/ScoreNotification/"
    val tessDataPath = "tessdata/"
    private val dataPath = basePath + tessDataPath
    private val languagePath = "$dataPath$language.traineddata"

    var textRecognizer: TextRecognizer? = null

    fun initalizeTess() {
        textRecognizer =  TextRecognizer.Builder(context).build()
        textRecognizer?.setProcessor(OcrDetectorProcessor{ f(it) })


        if (textRecognizer?.isOperational != true) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(tag, "Detector dependencies are not yet available.")

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            val lowstorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = context.registerReceiver(null, lowstorageFilter) != null

            if (hasLowStorage) {
                Toast.makeText(context, "low storage", Toast.LENGTH_LONG).show()
                Log.w(tag, "lowStorage")
            }
        }


    }

    fun processOcr(bitmap: Bitmap) {
        textRecognizer?.receiveFrame(Frame.Builder().setBitmap(bitmap).build())
    }
}
