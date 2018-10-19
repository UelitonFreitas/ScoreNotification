package com.notification.score.scorenotification.imageprovider

import android.graphics.Bitmap
import android.os.Handler
import android.view.TextureView
import com.google.android.exoplayer2.ui.PlayerView

class ImageProviderImpl(private var playerView: PlayerView, val onImageCaptured: (Bitmap) -> Unit) : ImageProvider {

    private var handler = Handler()
    private val runnable = Runnable { startToGetImagePeriodically() }

    override fun startToGetImagePeriodically() {
        getImage()
        handler.postDelayed(runnable, 500)
    }

    override fun getImage() {
        (playerView.videoSurfaceView as? TextureView)?.bitmap?.let { onImageCaptured(it) }
    }

    override fun stopToGetImagesPeriodically(){
        handler.removeCallbacks(runnable)
    }

}