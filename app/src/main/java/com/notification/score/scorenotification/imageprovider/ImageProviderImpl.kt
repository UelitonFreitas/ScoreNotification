package com.notification.score.scorenotification.imageprovider

import android.graphics.Bitmap
import android.view.TextureView
import com.globo.video.player.Player
import com.google.android.exoplayer2.ui.PlayerView

class ImageProviderImpl(private var player: Player) : ImageProvider {

    override fun getImage(onImageCaptured: (Bitmap) -> Unit) {
        ((player.core?.activePlayback?.view as? PlayerView)?.videoSurfaceView as? TextureView)?.bitmap?.let { onImageCaptured(it) }
    }
}