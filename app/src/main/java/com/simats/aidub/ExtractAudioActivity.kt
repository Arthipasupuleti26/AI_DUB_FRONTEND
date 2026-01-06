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

class ExtractAudioActivity : AppCompatActivity() {

    private lateinit var projectRepository: ProjectRepository
    private var projectId: String? = null
    private var videoUri: String? = null
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

        // Get data from intent
        videoUri = intent.getStringExtra("VIDEO_URI")
        projectId = intent.getStringExtra("PROJECT_ID")
        
        // Check if resuming from saved progress
        projectId?.let { id ->
            val project = projectRepository.getProject(id)
            if (project != null && project.processingStage == "extracting_audio") {
                startProgress = project.processingProgress
            }
        }

        // Start waveform animation
        startWaveformAnimation()

        // Start extraction progress simulation
        simulateExtraction()
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
            val animator = ObjectAnimator.ofFloat(bar, "scaleY", 0.3f, 1f, 0.3f)
            animator.duration = 800L + (index * 50L)
            animator.repeatCount = ValueAnimator.INFINITE
            animator.repeatMode = ValueAnimator.REVERSE
            animator.interpolator = LinearInterpolator()
            animator.startDelay = index * 80L
            animator.start()
        }
    }

    private fun simulateExtraction() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvPercentage = findViewById<TextView>(R.id.tv_percentage)
        val tvStatus = findViewById<TextView>(R.id.tv_status)

        // Set initial progress if resuming
        progressBar.progress = startProgress
        tvPercentage.text = "$startProgress%"

        lifecycleScope.launch {
            for (i in startProgress..100) {
                progressBar.progress = i
                tvPercentage.text = "$i%"

                // Update status message at certain thresholds
                val statusIndex = when {
                    i < 20 -> 0
                    i < 40 -> 1
                    i < 60 -> 2
                    i < 80 -> 3
                    else -> 4
                }
                tvStatus.text = statusMessages[statusIndex]

                // Save progress every 10%
                if (i % 10 == 0 && projectId != null) {
                    projectRepository.updateProjectProgress(projectId!!, "extracting_audio", i)
                }

                // Variable delay to simulate realistic processing
                val delayTime = when {
                    i < 20 -> (40..60).random().toLong()
                    i < 50 -> (30..50).random().toLong()
                    i < 80 -> (50..70).random().toLong()
                    else -> (60..80).random().toLong()
                }
                delay(delayTime)
            }

            // Extraction complete - update project stage and show completion
            delay(500)
            
            // Update project to next stage (transcribing)
            projectId?.let { id ->
                projectRepository.updateProjectProgress(id, "transcribing", 0)
            }
            
            Toast.makeText(this@ExtractAudioActivity, "Audio extraction complete!", Toast.LENGTH_SHORT).show()
            
            // Show Next Button
            val btnNext = findViewById<android.widget.Button>(R.id.btn_next)
            btnNext.visibility = View.VISIBLE
            btnNext.setOnClickListener {
                // Navigate to Transcription screen
                val intent = Intent(this@ExtractAudioActivity, TranscriptionActivity::class.java)
                intent.putExtra("PROJECT_ID", projectId)
                intent.putExtra("VIDEO_URI", videoUri)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        // Save current progress before going back
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        projectId?.let { id ->
            projectRepository.updateProjectProgress(id, "extracting_audio", progressBar.progress)
        }
        Toast.makeText(this, "Progress saved. You can resume from Recent Projects.", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
    }
}

