package com.simats.aidub

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.simats.aidub.repository.ProjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.activity.OnBackPressedCallback
import com.simats.aidub.model.ExtractAudioResponse
import com.simats.aidub.network.ApiClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class ExtractAudioActivity : AppCompatActivity() {

    private lateinit var projectRepository: ProjectRepository
    private var projectId: String? = null
    private var videoUri: String? = null            // EXISTING
    private var videoServerPath: String? = null     // ✅ ADDED
    private var startProgress: Int = 0

    private val statusMessages = listOf(
        "Separating vocal tracks...",
        "Analyzing audio frequencies...",
        "Isolating speech patterns...",
        "Processing audio segments...",
        "Finalizing extraction..."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_extract_audio)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize repository
        projectRepository = ProjectRepository(this)

        // Get intent data
        videoUri = intent.getStringExtra("VIDEO_URI")
        projectId = intent.getStringExtra("PROJECT_ID")

        if (videoUri.isNullOrBlank() || projectId.isNullOrBlank()) {
            Toast.makeText(this, "Invalid project data", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        videoServerPath = intent.getStringExtra("VIDEO_SERVER_PATH")
        android.util.Log.d("AUDIO", "SERVER PATH = $videoServerPath")


        // Resume saved progress if exists
        projectId?.let { id ->
            val project = projectRepository.getProject(id)
            if (project != null && project.processingStage == "extracting_audio") {
                startProgress = project.processingProgress
            }
        }

        startWaveformAnimation()
        startRealExtraction()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val progressBar = findViewById<ProgressBar>(R.id.progress_bar)

                projectId?.let {
                    projectRepository.updateProjectProgress(
                        it,
                        "extracting_audio",
                        progressBar.progress
                    )
                }

                Toast.makeText(
                    this@ExtractAudioActivity,
                    "Progress saved. You can resume later.",
                    Toast.LENGTH_SHORT
                ).show()

                isEnabled = false
                finish()
            }
        })
    }

    private fun startWaveformAnimation() {
        val bars = listOf(
            findViewById<View>(R.id.bar1),
            findViewById<View>(R.id.bar2),
            findViewById<View>(R.id.bar3),
            findViewById<View>(R.id.bar4),
            findViewById<View>(R.id.bar5),
            findViewById<View>(R.id.bar6),
            findViewById<View>(R.id.bar7),
            findViewById<View>(R.id.bar8),
            findViewById<View>(R.id.bar9),
            findViewById<View>(R.id.bar10)
        )

        bars.forEachIndexed { index, bar ->
            ObjectAnimator.ofFloat(bar, "scaleY", 0.3f, 1f, 0.3f).apply {
                duration = 800L + (index * 50L)
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = LinearInterpolator()
                startDelay = index * 80L
                start()
            }
        }
    }

    private fun startRealExtraction() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvStatus = findViewById<TextView>(R.id.tv_status)
        val btnNext = findViewById<android.widget.Button>(R.id.btn_next)

        tvStatus.text = "Extracting audio on server..."
        progressBar.isIndeterminate = true
        btnNext.visibility = View.GONE

        val api = ApiClient.apiService

        val projectIdBody =
            projectId!!.toRequestBody("text/plain".toMediaType())

        // ✅ ONLY FIX THAT MATTERS
        val videoPathBody =
            videoServerPath!!.toRequestBody("text/plain".toMediaType())

        api.extractAudio(projectIdBody, videoPathBody)
            .enqueue(object : retrofit2.Callback<ExtractAudioResponse> {

                override fun onResponse(
                    call: retrofit2.Call<ExtractAudioResponse>,
                    response: retrofit2.Response<ExtractAudioResponse>
                ) {
                    progressBar.isIndeterminate = false

                    if (response.isSuccessful && response.body()?.success == true) {

                        progressBar.progress = 100
                        tvStatus.text = "Audio extracted successfully"
                        AppNotifier.notifySuccess(this@ExtractAudioActivity,"Audio extracted successfully 🎧")
                        val audioPath = response.body()!!.audio_path!!

// SAVE AUDIO PATH IN LOCAL DB
                        projectRepository.updateAudioPath(projectId!!, audioPath)
                        projectRepository.updateProjectProgress(
                            projectId!!,
                            "transcribing",
                            0
                        )

                        btnNext.visibility = View.VISIBLE
                        btnNext.setOnClickListener {
                            startActivity(
                                Intent(
                                    this@ExtractAudioActivity,
                                    TranscriptionActivity::class.java
                                ).apply {
                                    putExtra("PROJECT_ID", projectId)
                                    putExtra("VIDEO_URI", videoUri)
                                }
                            )
                            finish()
                        }

                    } else {
                        Toast.makeText(
                            this@ExtractAudioActivity,
                            "Extraction failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<ExtractAudioResponse>,
                    t: Throwable
                ) {
                    progressBar.isIndeterminate = false
                    Toast.makeText(
                        this@ExtractAudioActivity,
                        t.message ?: "Server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // ❌ NOT REMOVED
    private fun simulateExtraction() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvPercentage = findViewById<TextView>(R.id.tv_percentage)
        val tvStatus = findViewById<TextView>(R.id.tv_status)
        val btnNext = findViewById<android.widget.Button>(R.id.btn_next)

        progressBar.progress = startProgress
        tvPercentage.text = "$startProgress%"
        btnNext.visibility = View.GONE

        lifecycleScope.launch {
            for (i in startProgress..100) {
                progressBar.progress = i
                tvPercentage.text = "$i%"

                val statusIndex = when {
                    i < 20 -> 0
                    i < 40 -> 1
                    i < 60 -> 2
                    i < 80 -> 3
                    else -> 4
                }
                tvStatus.text = statusMessages[statusIndex]

                if (i % 10 == 0 && projectId != null) {
                    projectRepository.updateProjectProgress(
                        projectId!!,
                        "extracting_audio",
                        i
                    )
                }

                delay(50)
            }

            projectId?.let {
                projectRepository.updateProjectProgress(it, "transcribing", 0)
            }

            Toast.makeText(
                this@ExtractAudioActivity,
                "Audio extraction complete!",
                Toast.LENGTH_SHORT
            ).show()

            btnNext.visibility = View.VISIBLE
            btnNext.setOnClickListener {
                val intent = Intent(
                    this@ExtractAudioActivity,
                    TranscriptionActivity::class.java
                )
                intent.putExtra("PROJECT_ID", projectId)
                intent.putExtra("VIDEO_URI", videoUri)
                startActivity(intent)
            }
        }
    }
}
