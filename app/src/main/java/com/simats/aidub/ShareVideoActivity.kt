package com.simats.aidub

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.speech.tts.TextToSpeech
import android.widget.VideoView
import com.simats.aidub.repository.ProjectRepository
import java.util.Locale

class ShareVideoActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var projectRepository: ProjectRepository
    private var projectId: String? = null
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_share_video)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = ProjectRepository(this)
        projectId = intent.getStringExtra("PROJECT_ID")
        tts = TextToSpeech(this, this)

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        setupVideoPlayback()
        setupSocialButtons()
        setupCopyLink()
    }

    private fun setupVideoPlayback() {
        val videoView = findViewById<VideoView>(R.id.video_preview_share)
        val tvSubtitle = findViewById<TextView>(R.id.tv_subtitle_share)

        projectId?.let { id ->
            val project = projectRepository.getProject(id)
            project?.videoUri?.let { uriString ->
                videoView.setVideoURI(android.net.Uri.parse(uriString))
                videoView.setOnPreparedListener { mp ->
                    mp.setVolume(0f, 0f) // Mute original
                    mp.isLooping = true
                    videoView.start()
                    
                    // Start English TTS
                    project.translatedText?.let { text ->
                        tts?.setPitch(project.pitch)
                        tts?.setSpeechRate(project.speed)
                        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SharePreview")
                    }
                    tvSubtitle.text = project.translatedText
                    tvSubtitle.visibility = android.view.View.VISIBLE
                }
            }
        }
    }

    private fun setupSocialButtons() {
        findViewById<LinearLayout>(R.id.btn_share_youtube).setOnClickListener {
            shareToSocial("YouTube")
        }
        findViewById<LinearLayout>(R.id.btn_share_instagram).setOnClickListener {
            shareToSocial("Instagram")
        }
        findViewById<LinearLayout>(R.id.btn_share_facebook).setOnClickListener {
            shareToSocial("Facebook")
        }
        findViewById<LinearLayout>(R.id.btn_share_save).setOnClickListener {
            projectId?.let { id ->
                val project = projectRepository.getProject(id)
                project?.videoUri?.let { uriString ->
                    val sourceUri = android.net.Uri.parse(uriString)
                    val res = project.selectedResolution ?: "1080p"
                    val format = project.selectedFormat ?: "MP4"
                    val fileName = "AIDUB_${project.title.replace(" ", "_")}_$res.${format.lowercase()}"
                    
                    saveVideoToGallery(sourceUri, fileName)
                }
            }
        }
    }

    private fun saveVideoToGallery(videoUri: android.net.Uri, fileName: String) {
        val resolver = contentResolver
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.Video.Media.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(android.provider.MediaStore.Video.Media.RELATIVE_PATH, "Movies/AIDUB")
                put(android.provider.MediaStore.Video.Media.IS_PENDING, 1)
            }
        }

        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            android.provider.MediaStore.Video.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val itemUri = resolver.insert(collection, contentValues)

        itemUri?.let { uri ->
            try {
                Toast.makeText(this, "Saving to Gallery...", Toast.LENGTH_SHORT).show()
                resolver.openOutputStream(uri).use { outputStream ->
                    resolver.openInputStream(videoUri).use { inputStream ->
                        inputStream?.copyTo(outputStream!!)
                    }
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(android.provider.MediaStore.Video.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
                
                Toast.makeText(this, "Successfully saved to Gallery!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    private fun shareToSocial(platform: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_TEXT, "Check out my dubbed video using AI DUB!")
        }
        startActivity(Intent.createChooser(shareIntent, "Share to $platform"))
    }

    private fun setupCopyLink() {
        findViewById<LinearLayout>(R.id.btn_copy_link).setOnClickListener {
            val link = findViewById<TextView>(R.id.tv_share_link).text.toString()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Share Link", link)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }
}
