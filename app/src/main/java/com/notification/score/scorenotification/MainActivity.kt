package com.notification.score.scorenotification

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat


class MainActivity : AppCompatActivity() {

    private val camera = object :  CameraBridgeViewBase.CvCameraViewListener {
        override fun onCameraViewStarted(width: Int, height: Int) {
        }

        override fun onCameraViewStopped() {
        }

        override fun onCameraFrame(inputFrame: Mat?): Mat {
            Log.d("@@@", "frame")
            return inputFrame!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val mOpenCvCameraView = findViewById<CameraBridgeViewBase>(R.id.live_camera_frame)
        mOpenCvCameraView.setCvCameraViewListener(camera)
        // Example of a call to a native method
//        sample_text.text = stringFromJNI()
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
