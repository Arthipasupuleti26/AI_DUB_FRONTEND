package com.simats.aidub

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
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

class VoiceGenerationActivity : AppCompatActivity() {

    private lateinit var projectRepository: ProjectRepository
    private var projectId: String? = null
    private var startProgress: Int = 0

    private val statusMessages = listOf(
        "Analyzing text emotion...",
        "Selecting voice profile...",
        "Synthesizing speech...",
        "Adjusting pitch and tone...",
        "Mixing final audio..."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voice_generation)
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
            if (project != null && project.processingStage == "generating_voice") {
                startProgress = project.processingProgress
            }
        }
        
        startWaveformAnimation()
        simulateGeneration()
    }

    private fun startWaveformAnimation() {
        val bars = listOf(
            findViewById<View>(R.id.bar1),
            findViewById<View>(R.id.bar2),
            findViewById<View>(R.id.bar3),
            findViewById<View>(R.id.bar4),
            findViewById<View>(R.id.bar5)
        )

        bars.forEachIndexed { index, bar ->
            val animator = ObjectAnimator.ofFloat(bar, "scaleY", 0.3f, 1f, 0.3f)
            animator.duration = 600L + (index * 100L)
            animator.repeatCount = ValueAnimator.INFINITE
            animator.repeatMode = ValueAnimator.REVERSE
            animator.interpolator = LinearInterpolator()
            animator.startDelay = index * 50L
            animator.start()
        }
    }

    private fun simulateGeneration() {
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

                // Update status message
                val statusIndex = when {
                    i < 20 -> 0
                    i < 40 -> 1
                    i < 70 -> 2
                    i < 90 -> 3
                    else -> 4
                }
                tvStatus.text = statusMessages[statusIndex]

                // Save progress periodically
                if (i % 10 == 0 && projectId != null) {
                    projectRepository.updateProjectProgress(projectId!!, "generating_voice", i)
                }

                val delayTime = (50..100).random().toLong()
                delay(delayTime)
            }

            // Generation complete
            delay(500)
            onGenerationComplete()
        }
    }

    private fun onGenerationComplete() {
        Toast.makeText(this, "Voice generation complete!", Toast.LENGTH_SHORT).show()
        
        // Show Next Button
        val btnNext = findViewById<Button>(R.id.btn_next)
        btnNext.visibility = View.VISIBLE
        btnNext.setOnClickListener {
             // Navigate to Select Voice
             val intent = Intent(this@VoiceGenerationActivity, SelectVoiceActivity::class.java)
             intent.putExtra("PROJECT_ID", projectId)
             startActivity(intent)
             finish()
        }
    }

    override fun onBackPressed() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        projectId?.let { id ->
            projectRepository.updateProjectProgress(id, "generating_voice", progressBar.progress)
        }
        Toast.makeText(this, "Progress saved.", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
    }
}
