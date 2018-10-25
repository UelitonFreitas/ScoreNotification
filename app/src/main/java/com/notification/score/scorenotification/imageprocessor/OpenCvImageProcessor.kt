package com.notification.score.scorenotification.imageprocessor

import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class OpenCvImageProcessor: ImageProcessor {

    override fun processImage(image: Bitmap, onImageProcessed: (Bitmap) -> Unit) {
        var placar: Bitmap? = null

        Log.d("@@@", "Bitmap: ${image.height}/${image.width}")
        placar = Bitmap.createBitmap(image, 10, image.height / 20, image.width / 4, image.height / 10)

        val img_mat = Mat()
        Utils.bitmapToMat(placar, img_mat)

        val img_gray = Mat()

        Imgproc.cvtColor(img_mat, img_gray, Imgproc.COLOR_BGR2GRAY)

        val img_blurred = Mat()

        Imgproc.GaussianBlur(img_gray, img_blurred, Size(3.0, 3.0), 2.0)

        Utils.matToBitmap(img_gray, placar)

        onImageProcessed(placar)
    }
}