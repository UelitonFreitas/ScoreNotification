package com.notification.score.scorenotification

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.globo.video.player.Player
import com.globo.video.player.PlayerOption
import com.globo.video.player.base.PlayerMimeType
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.notification.score.scorenotification.classifiers.FirebaseVisionDocumentTextRecognizer
import com.notification.score.scorenotification.classifiers.ScoreClassifier
import com.notification.score.scorenotification.classifiers.ScoreClassifierImpl
import com.notification.score.scorenotification.imageprovider.ImageProvider
import com.notification.score.scorenotification.imageprovider.ImageProviderImpl
import io.clappr.player.base.ClapprOption
import io.clappr.player.base.Options


class MainActivity() : AppCompatActivity() {
    private lateinit var globoPlayer : Player
    private lateinit var imageView: ImageView
    private lateinit var playerContainer: ViewGroup
    private val scores: TextView by lazy { findViewById<TextView>(R.id.text_view_score) }
    lateinit var imageProvider: ImageProvider
    lateinit var scoreClassifier: ScoreClassifier

    lateinit var scoreRecognizer: ScoreRecognizer

    private val cameraRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        io.clappr.player.Player.initialize(applicationContext)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        playerContainer = findViewById(R.id.player_container)

        scoreClassifier = FirebaseVisionDocumentTextRecognizer(this.applicationContext).apply { start() }

        globoPlayer = Player()

        val options = hashMapOf<String, Any>(
                PlayerOption.TOKEN.value to "10c2198d248307aef236cf5ba92a24cc751655a7a3445734f454542734c65694e6d6d7744596b664e6a2d35503570795733766139445846576b47506433715157546c55374a715a65375a51746571374b75396c4e684242756b6d5a537466755f6174784b73513d3d3a303a74657374653939392e76656c6f782e3035",
                ClapprOption.START_AT.value to 280)
        globoPlayer.configure(Options("6851541", PlayerMimeType.VIDEO_ID.value, options))

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.player_container, globoPlayer)
        fragmentTransaction.commit()
    }


    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), cameraRequestCode)
        } else { }

        imageProvider = ImageProviderImpl(globoPlayer)
        scoreClassifier = ScoreClassifierImpl(this).apply { start() }
        scoreRecognizer = ScoreRecognizer(imageProvider, scoreClassifier, ::onScoreFound).apply {
            onImageProcessed = ::onImageProcessed
            startWatchScoreChange()
        }
    }

    private fun onImageProcessed(bitmap: Bitmap) {
        runOnUiThread { imageView.setImageBitmap(bitmap) }
    }


    override fun onDestroy() {
        scoreRecognizer.stopWatchScoreChange()
        super.onDestroy()

    }

    private fun onScoreFound(score: String) {
        runOnUiThread { scores.text = score }
    }

    fun buttonClick(v: View) {
        scoreRecognizer.findScore()
        val playerBitmap = globoPlayer.getVideoFrame()
        val videoBitmap = ((globoPlayer.core?.activePlayback?.view as? PlayerView)?.videoSurfaceView as? TextureView)?.bitmap
        Log.d("Scorenotification", "onImageProcessed: (${playerBitmap?.width}/${playerBitmap?.height}) / (${videoBitmap?.width}/${videoBitmap?.height})")
    }
}


