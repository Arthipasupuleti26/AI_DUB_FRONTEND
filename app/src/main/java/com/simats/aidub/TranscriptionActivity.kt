package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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
import kotlinx.coroutines.flow.collect

class TranscriptionActivity : AppCompatActivity() {

    private lateinit var projectRepository: ProjectRepository
    private var projectId: String? = null
    private var startProgress: Int = 0

    private val statusMessages = listOf(
        "Loading audio data...",
        "Identifying speakers...",
        "Converting speech to text...",
        "Formatting subtitles...",
        "Finalizing transcription..."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transcription)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = ProjectRepository(this)
        projectId = intent.getStringExtra("PROJECT_ID")

        // Resume progress if exists
        projectId?.let { id ->
            val project = projectRepository.getProject(id)
            if (project != null && project.processingStage == "transcribing") {
                startProgress = project.processingProgress
            }
        }

        simulateTranscription()
    }

    private fun simulateTranscription() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvPercentage = findViewById<TextView>(R.id.tv_percentage)
        val tvStatus = findViewById<TextView>(R.id.tv_status)
        val scrollView = findViewById<android.widget.ScrollView>(R.id.scroll_text)
        val textContainer = findViewById<android.widget.LinearLayout>(R.id.text_container)
        
        // Clear existing static views
        if (startProgress == 0) {
            textContainer.removeAllViews()
            val spacer = View(this)
            val params = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 
                150
            )
            spacer.layoutParams = params
            textContainer.addView(spacer)
        }

        // Set initial progress
        progressBar.progress = startProgress
        tvPercentage.text = "$startProgress%"

        lifecycleScope.launch {
            // Start the transcription service
            val transcriptionService = com.simats.aidub.service.TranscriptionService()
            
            // Collect the text stream dynamically
            launch {
                transcriptionService.transcribeVideo(intent.getStringExtra("VIDEO_URI"))
                    .collect { lineText ->
                        // Add new line to UI
                        val textView = TextView(this@TranscriptionActivity)
                        textView.text = lineText
                        textView.textSize = 16f
                        textView.setTextColor(android.graphics.Color.parseColor("#1F2937"))
                        textView.setTypeface(null, android.graphics.Typeface.BOLD)
                        val params = android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 0, 0, 40)
                        textView.layoutParams = params
                        
                        if (textContainer.childCount > 0) {
                             textContainer.addView(textView, textContainer.childCount - 1)
                        } else {
                             textContainer.addView(textView)
                        }
                        
                        textView.alpha = 0f
                        textView.animate().alpha(1f).setDuration(300).start()
                        
                        scrollView.post { 
                            scrollView.fullScroll(View.FOCUS_DOWN)
                        }
                    }
            }

            // Simulate progress bar independently (since real progress depends on file size)
            for (i in startProgress..100) {
                progressBar.progress = i
                tvPercentage.text = "$i%"

                val statusIndex = when {
                    i < 20 -> 0
                    i < 40 -> 1
                    i < 70 -> 2
                    i < 90 -> 3
                    else -> 4
                }
                tvStatus.text = statusMessages[statusIndex]

                if (i % 10 == 0 && projectId != null) {
                    projectRepository.updateProjectProgress(projectId!!, "transcribing", i)
                }

                val delayTime = (50..100).random().toLong()
                delay(delayTime)
            }

            // Complete
            delay(500)
            onTranscriptionComplete()
        }
    }

    private fun onTranscriptionComplete() {
        // Update to next stage (translating or complete)
        projectId?.let { id ->
            projectRepository.updateProjectProgress(id, "translating", 0)
        }
        
        Toast.makeText(this, "Transcription complete!", Toast.LENGTH_SHORT).show()
        
        // Show Next Button
        val btnNext = findViewById<Button>(R.id.btn_next)
        btnNext.visibility = View.VISIBLE
        btnNext.setOnClickListener {
             // Navigate to Translation screen
             val intent = Intent(this@TranscriptionActivity, TranslationActivity::class.java)
             intent.putExtra("PROJECT_ID", projectId)
             startActivity(intent)
             finish()
        }
    }

    override fun onBackPressed() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        projectId?.let { id ->
            projectRepository.updateProjectProgress(id, "transcribing", progressBar.progress)
        }
        Toast.makeText(this, "Progress saved.", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
    }
}
