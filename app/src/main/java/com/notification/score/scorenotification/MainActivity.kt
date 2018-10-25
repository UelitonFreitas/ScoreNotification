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
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.notification.score.scorenotification.classifiers.ScoreClassifier
import com.notification.score.scorenotification.classifiers.ScoreClassifierImpl
import com.notification.score.scorenotification.imageprocessor.OpenCvImageProcessor
import com.notification.score.scorenotification.imageprovider.ImageProvider
import com.notification.score.scorenotification.imageprovider.ImageProviderImpl
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader


class MainActivity() : AppCompatActivity() {

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    private lateinit var playerView: PlayerView
    private lateinit var imageView: ImageView
    private val scores: TextView by lazy { findViewById<TextView>(R.id.text_view_score) }
    lateinit var imageProvider: ImageProvider
    lateinit var scoreClassifier: ScoreClassifier

    lateinit var scoreRecognizer: ScoreRecognizer

    private val cameraRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
        imageView = findViewById(R.id.imageView)

        val player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        playerView.player = player
        val mediaSource = ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, Util.getUserAgent(this, "ScoreNotification"))).createMediaSource(Uri.parse("asset:///Brasil_Mexico_2oT_720.mp4"));
        player.prepare(mediaSource)
    }


    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), cameraRequestCode)
        } else {
            initOpenCv()
        }

        imageProvider = ImageProviderImpl(playerView)
        scoreClassifier = ScoreClassifierImpl(this).apply { start() }
        scoreRecognizer = ScoreRecognizer(imageProvider, scoreClassifier, ::onScoreFound).apply {
            onImageProcessed = ::onImageProcessed
            startWatchScoreChange()
        }
    }

    private fun onImageProcessed(bitmap: Bitmap) {
        runOnUiThread { imageView.setImageBitmap(bitmap) }
    }

    private fun initOpenCv() {
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == cameraRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initOpenCv()
            }
        }
    }

    override fun onDestroy() {
        scoreRecognizer.stopWatchScoreChange()
        super.onDestroy()

    }

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i("opencv", "opencv success")
                    scoreRecognizer.imageProcessor = OpenCvImageProcessor()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    private fun onScoreFound(score: String) {
        runOnUiThread { scores.text = score }
    }

    fun buttonClick(v: View) {
        scoreRecognizer.findScore()
    }
}


