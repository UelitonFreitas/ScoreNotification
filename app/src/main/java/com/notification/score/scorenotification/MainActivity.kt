package com.notification.score.scorenotification

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.globo.video.player.Player
import com.globo.video.player.PlayerOption
import com.globo.video.player.base.PlayerMimeType
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.notification.score.scorenotification.classifiers.FaceDetectImpl
import com.notification.score.scorenotification.classifiers.ImageClassifier
import com.notification.score.scorenotification.imageprovider.ImageProvider
import com.notification.score.scorenotification.imageprovider.ImageProviderImpl
import io.clappr.player.base.ClapprOption
import io.clappr.player.base.Options


class MainActivity() : AppCompatActivity() {
    private lateinit var globoPlayer: Player
    private lateinit var imageView: ImageView
    private lateinit var playerContainer: ViewGroup
    private val scores: TextView by lazy { findViewById<TextView>(R.id.text_view_score) }
    lateinit var imageProvider: ImageProvider

    lateinit var imageClassifier: ImageClassifier<List<FirebaseVisionFace>>
    lateinit var scoreRecognizer: ObjectRecognizer<List<FirebaseVisionFace>>

    override fun onCreate(savedInstanceState: Bundle?) {
        io.clappr.player.Player.initialize(applicationContext)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        playerContainer = findViewById(R.id.player_container)

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

        imageProvider = ImageProviderImpl(globoPlayer)
        imageClassifier = FaceDetectImpl()
        scoreRecognizer = ObjectRecognizer(imageProvider, imageClassifier, ::onFacesFound, ::onDrawRequest).apply {
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

    private fun onFacesFound(faces: List<FirebaseVisionFace>) {
        scores.text = "${faces.size} faces found!"
    }

    private fun onDrawRequest(bitmap: Bitmap) {
        onImageProcessed(bitmap)
    }
}


