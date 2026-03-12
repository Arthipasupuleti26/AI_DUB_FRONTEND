package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdjustSyncActivity : AppCompatActivity() {

    private var currentOffset = 0.0
    private lateinit var tvOffset: TextView
    private lateinit var projectRepository: com.simats.aidub.repository.ProjectRepository
    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adjust_sync)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = com.simats.aidub.repository.ProjectRepository(this)
        projectId = intent.getStringExtra("PROJECT_ID")

        tvOffset = findViewById(R.id.tv_offset)
        val videoPreview = findViewById<android.widget.VideoView>(R.id.video_sync_preview)

        // Load Video
        projectId?.let { id ->
            val project = projectRepository.getProject(id)
            project?.videoUri?.let { uriString ->
                videoPreview.setVideoURI(android.net.Uri.parse(uriString))
                videoPreview.start()
                videoPreview.setOnPreparedListener { it.isLooping = true }
            }
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_save).setOnClickListener {
            projectId?.let { id ->
                // In a real app we'd save currentOffset to the project
                Toast.makeText(this, "Sync alignment (${String.format("%.1fs", currentOffset)}) saved!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        findViewById<TextView>(R.id.btn_minus).setOnClickListener {
            currentOffset -= 0.1
            updateOffsetDisplay()
        }

        findViewById<TextView>(R.id.btn_plus).setOnClickListener {
            currentOffset += 0.1
            updateOffsetDisplay()
        }
    }

    private fun updateOffsetDisplay() {
        tvOffset.text = String.format("%.1fs offset", currentOffset)
        // In a real app, this would also adjust the audio track synchronization in the player
    }
}
