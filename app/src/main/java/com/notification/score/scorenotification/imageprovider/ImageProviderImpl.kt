package com.notification.score.scorenotification.imageprovider

import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import android.view.TextureView
import com.google.android.exoplayer2.ui.PlayerView
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ImageProviderImpl(private var playerView: PlayerView, val onImageCaptured: (Bitmap) -> Unit) : ImageProvider {

    private var handler = Handler()
    private val runnable = Runnable { startToGetImagePeriodically() }

    override fun startToGetImagePeriodically() {
        getImage()
        handler.postDelayed(runnable, 500)
    }

    override fun getImage() {
        (playerView.videoSurfaceView as? TextureView)?.bitmap?.let {

            var placar: Bitmap? = null

            Log.d("@@@", "Bitmap: ${it.height}/${it.width}")
            placar = Bitmap.createBitmap(it, 10, it.height / 20, it.width / 4, it.height / 10)

            val img_mat = Mat()
            Utils.bitmapToMat(placar, img_mat)

            val img_gray = Mat()

            Imgproc.cvtColor(img_mat, img_gray, Imgproc.COLOR_BGR2GRAY)

            val img_blurred = Mat()

            Imgproc.GaussianBlur(img_gray, img_blurred, Size(3.0, 3.0), 2.0)

            Utils.matToBitmap(img_gray, placar)

            onImageCaptured(placar)
        }
    }

    override fun stopToGetImagesPeriodically() {
        handler.removeCallbacks(runnable)
    }

}