package com.notification.score.scorenotification

import android.graphics.Bitmap
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.Utils
import org.opencv.core.Mat

class Camera(private val onCamera: ((Bitmap) -> Unit)?): CameraBridgeViewBase.CvCameraViewListener2 {

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        val mat = inputFrame?.rgba()
        val bitmap = Bitmap.createBitmap(mat?.cols()!!, mat.rows(), Bitmap.Config.RGB_565)
        Utils.matToBitmap(mat, bitmap);
        onCamera?.invoke(bitmap)
        return mat
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
    }

    override fun onCameraViewStopped() {
    }
}