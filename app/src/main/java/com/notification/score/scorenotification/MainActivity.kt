package com.notification.score.scorenotification

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import android.view.SurfaceView
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat


class MainActivity() : AppCompatActivity() {

    private val camera = object :  CameraBridgeViewBase.CvCameraViewListener2 {
        override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
            Log.d("@@@", "frame")
            return inputFrame?.rgba()!!
        }

        override fun onCameraViewStarted(width: Int, height: Int) {
        }

        override fun onCameraViewStopped() {
        }
    }

    lateinit private var playerView : PlayerView

    private var mOpenCvCameraView : CameraBridgeViewBase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)

        val player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        playerView.player = player
        val mediaSource = ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, Util.getUserAgent(this, "ScoreNotification"))).createMediaSource(Uri.parse("asset:///highline.mp4"));
        player.prepare(mediaSource)

        mOpenCvCameraView = findViewById(R.id.live_camera_frame)
        mOpenCvCameraView?.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView?.setCvCameraViewListener(camera)

        // Example of a call to a native method
//        sample_text.text = stringFromJNI()
    }

    override fun onPause() {
        super.onPause()
        mOpenCvCameraView?.disableView()
    }

    override fun onResume() {
        super.onResume()
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback)
    }


    override fun onDestroy() {
        super.onDestroy()
        mOpenCvCameraView?.disableView()
    }

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i("opencv", "opencv success")
                    mOpenCvCameraView?.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
