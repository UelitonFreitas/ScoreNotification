package com.notification.score.scorenotification.imageprovider

import android.graphics.Bitmap
import android.view.TextureView
import com.google.android.exoplayer2.ui.PlayerView

class ImageProviderImpl(private var playerView: PlayerView) : ImageProvider {

    override fun getImage(onImageCaptured: (Bitmap) -> Unit) {
        (playerView.videoSurfaceView as? TextureView)?.bitmap?.let { onImageCaptured(it) }
    }
}