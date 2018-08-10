package com.notification.score.scorenotification

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import org.jetbrains.anko.doAsync
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


class MainActivity() : AppCompatActivity() {

    lateinit private var playerView: PlayerView
    lateinit private var imageView: ImageView
    val scores by lazy { findViewById<TextView>(R.id.text_view_score) }
    lateinit var ocr: TessOCR

    private var handler = Handler()
    private val runnable = Runnable { getImagePeriodically() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
        imageView = findViewById(R.id.imageView)
        ocr = TessOCR(this) {
            runOnUiThread{
                scores.text = it
            }
        }

        val player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        playerView.player = player
        val mediaSource = ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, Util.getUserAgent(this, "ScoreNotification"))).createMediaSource(Uri.parse("asset:///Brasil_Mexico_2oT_720.mp4"));
        player.prepare(mediaSource)

        //getImagePeriodically()

        initOpenCv()

    }

    private val cameraRequestCode = 100

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), cameraRequestCode)
        } else {
            initOpenCv()
        }
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
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i("opencv", "opencv success")
                    doAsync {
                        ocr.initalizeTess()
                    }
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    fun getImagePeriodically() {
        getImage()
        handler.postDelayed(runnable, 1000)
    }

    fun buttonClick(v: View) {
        getImage()
    }

    /*fun getImage() {
        val textureView = playerView.getVideoSurfaceView() as TextureView
        val bitmap = textureView.bitmap

        doAsync {
            var text: String? = null
            var placar : Bitmap? = null

            bitmap?.let {
                Log.d("@@@", "Bitmap: ${bitmap?.height}/${bitmap?.width}")
                placar = Bitmap.createBitmap(it, 10, it.height / 20, it.width / 4, it.height / 10)

                val img_mat = Mat()
                Utils.bitmapToMat(placar, img_mat)

                val img_gray = Mat()

                Imgproc.cvtColor(img_mat, img_gray, Imgproc.COLOR_BGR2GRAY)

                val img_blurred = Mat()

                Imgproc.GaussianBlur(img_gray, img_blurred,  Size(3.0,3.0), 2.0)

                Utils.matToBitmap(img_gray, placar)
                ocr.processOcr(placar!!)
            }

            runOnUiThread {
                imageView.setImageBitmap(placar)
            }
        }
//        imageView.setImageBitmap(bitmap)
    }*/

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

    fun getImage() {
        val textureView = playerView.getVideoSurfaceView() as TextureView
        val bitmap = textureView.getBitmap()
        bitmap?.let {
            Log.d("@@@", "Bitmap: ${bitmap?.height}/${bitmap?.width}")
            val placar = Bitmap.createBitmap(it, 50, it.height/60, it.width/3, it.height/10)

            try {
                val results = get_scores(placar)
                imageView.setImageBitmap(results.get(0))
            }catch (e: Exception)
            {
                imageView.setImageBitmap(placar)
            }
        }
    }

    fun get_scores(image_bitmap : Bitmap): MutableList<Bitmap>
    {

        val img_mat = Mat()
        Utils.bitmapToMat(image_bitmap, img_mat)

        val img_gray = Mat()

        Imgproc.cvtColor(img_mat, img_gray, Imgproc.COLOR_BGR2GRAY)

        val img_blurred = Mat()

        Imgproc.GaussianBlur(img_gray, img_blurred,  Size(3.0,3.0), 2.0)

        val edges = Mat()

        Imgproc.Canny(img_blurred, edges, 300.0, 600.0)

        val points: List<MatOfPoint> = ArrayList()

        val hierarchy = Mat();

        Imgproc.findContours(edges, points, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        val contours_area: MutableList<MatOfPoint> = ArrayList()

        for (ctn in points){
            if(Imgproc.contourArea(ctn) > 15 && Imgproc.contourArea(ctn) < 25)
            {
                contours_area.add(ctn)
            }
        }

        val rect_1: Rect = Imgproc.boundingRect(contours_area.get(contours_area.size - 1))
        val rect_2: Rect = Imgproc.boundingRect(contours_area.get(contours_area.size - 2))

        val img_number_1 : Mat = img_mat.submat(rect_1.y, rect_1.y + rect_1.height, rect_1.x, rect_1.x + rect_1.width);
        val img_number_2 : Mat = img_mat.submat(rect_2.y, rect_2.y + rect_2.height, rect_2.x, rect_2.x + rect_2.width);

        val img_number_1_resized = Mat()
        val img_number_2_resized = Mat()

        Imgproc.resize(img_number_1, img_number_1_resized, Size(20.0,20.0))
        Imgproc.resize(img_number_2, img_number_2_resized, Size(20.0,20.0))

        val img__number_1_binary = Mat()
        val img__number_2_binary = Mat()

        Imgproc.threshold(img_number_1_resized, img__number_1_binary, 127.0, 255.0, Imgproc.THRESH_BINARY)
        Imgproc.threshold(img_number_2_resized, img__number_2_binary, 127.0, 255.0, Imgproc.THRESH_BINARY)

        val img_roi = img_mat.clone()

        Imgproc.rectangle(img_roi, Point(rect_1.x.toDouble(), rect_1.y.toDouble()), Point(rect_1.x.toDouble() + rect_1.width, rect_1.y.toDouble() + rect_1.height), Scalar(0.0,255.0,0.0), 1)
        Imgproc.rectangle(img_roi, Point(rect_2.x.toDouble(), rect_2.y.toDouble()), Point(rect_2.x.toDouble() + rect_2.width, rect_2.y.toDouble() + rect_2.height), Scalar(0.0,255.0,0.0), 1)

        //Values Returned
        val img_roi_bitmap : Bitmap = Bitmap.createBitmap(img_roi.cols(), img_roi.rows(), Bitmap.Config.RGB_565)
        val img__number_1_bitmap : Bitmap = Bitmap.createBitmap(img__number_1_binary.cols(), img__number_1_binary.rows(), Bitmap.Config.RGB_565)
        val img_number_2_bitmap : Bitmap = Bitmap.createBitmap(img__number_2_binary.cols(), img__number_2_binary.rows(), Bitmap.Config.RGB_565)

        Utils.matToBitmap(img_roi, img_roi_bitmap)
        Utils.matToBitmap(img__number_1_binary, img__number_1_bitmap)
        Utils.matToBitmap(img_number_2_resized, img_number_2_bitmap)

        val return_values: MutableList<Bitmap> = ArrayList()

        return_values.add(img_roi_bitmap)
        return_values.add(img__number_1_bitmap)
        return_values.add(img_number_2_bitmap)

        return return_values
    }


}


