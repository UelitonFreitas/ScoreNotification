package com.notification.score.scorenotification

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.globo.video.player.Player
import com.globo.video.player.PlayerOption
import com.globo.video.player.base.ErrorCode
import com.globo.video.player.base.PlayerMimeType
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.notification.score.scorenotification.classifiers.FaceDetectImpl
import com.notification.score.scorenotification.classifiers.ImageClassifier
import com.notification.score.scorenotification.imageprovider.ImageProvider
import com.notification.score.scorenotification.imageprovider.ImageProviderImpl
import io.clappr.player.base.Callback
import io.clappr.player.base.ClapprOption
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.base.ErrorInfo




class MainActivity() : AppCompatActivity() {
    private lateinit var globoPlayer: Player
    data class VideoSelection(val description: String, val id: Int) {
        override fun toString(): String = description
    }

    val videoItens: Array<VideoSelection> = arrayOf(
            VideoSelection("BRASIL X MEXICO 2 TEMPO - HD 1080", 6851541),
            VideoSelection("BRASIL X MEXICO 1 TEMPO - HD", 6851536)
    )

    val tokens: Array<String> = arrayOf(
            "",
            "df5e06e7e461aac43f79eae77fea5a9e",
            "10c2198d248307aef236cf5ba92a24cc751655a7a3445734f454542734c65694e6d6d7744596b664e6a2d35503570795733766139445846576b47506433715157546c55374a715a65375a51746571374b75396c4e684242756b6d5a537466755f6174784b73513d3d3a303a74657374653939392e76656c6f782e3035"
    )
    private var availableTokens: MutableList<String> = mutableListOf()

    private lateinit var imageView: ImageView
    private lateinit var playerContainer: ViewGroup
    private lateinit var videoSelectionSpinner: Spinner
    private lateinit var startAtEdit: EditText

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
        videoSelectionSpinner = findViewById(R.id.video_spinner)
        startAtEdit = findViewById(R.id.edit_text_start)

        ArrayAdapter<VideoSelection>(this,
                android.R.layout.simple_spinner_item,
                videoItens
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            videoSelectionSpinner.adapter = adapter
        }

        globoPlayer = Player()

        globoPlayer.on(Event.ERROR.value, Callback.wrap { bundle -> reload(bundle) })

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.player_container, globoPlayer)
        fragmentTransaction.commit()
    }


    override fun onResume() {
        super.onResume()

        imageProvider = ImageProviderImpl(globoPlayer)
        imageClassifier = FaceDetectImpl()
        scoreRecognizer = ObjectRecognizer(imageProvider, imageClassifier, ::onFacesFound, ::onObjectsFoundDrawed).apply {
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

    private fun onObjectsFoundDrawed(bitmap: Bitmap) {
        onImageProcessed(bitmap)
    }

    fun loadClicked(v: View) {
        globoPlayer.stop()

        availableTokens = tokens.toMutableList()

        loadVideo()
    }

    private fun loadVideo() {
        if (availableTokens.isEmpty()) return

        val token = availableTokens.removeAt(0)
        val videoId = (videoSelectionSpinner.selectedItem as VideoSelection).id.toString()
        val startAt = startAtEdit.text.toString().toIntOrNull() ?: 0

        val options = hashMapOf(
                PlayerOption.TOKEN.value to token,
                ClapprOption.START_AT.value to startAt)
        globoPlayer.configure(Options(videoId, PlayerMimeType.VIDEO_ID.value, options))
    }

    private fun reload(errorBundle: Bundle?) {
        val errorInfo = errorBundle?.getParcelable<ErrorInfo>(Event.ERROR.value)
        if (errorInfo?.code == ErrorCode.AUTHENTICATION) {
            loadVideo()
        }
    }
}


