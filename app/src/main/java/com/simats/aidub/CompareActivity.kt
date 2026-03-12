package com.simats.aidub

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CompareActivity : AppCompatActivity() {

    private lateinit var projectRepository: com.simats.aidub.repository.ProjectRepository
    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_compare)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = com.simats.aidub.repository.ProjectRepository(this)
        projectId = intent.getStringExtra("PROJECT_ID")

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        val videoOriginal = findViewById<android.widget.VideoView>(R.id.video_original)
        val videoDubbed = findViewById<android.widget.VideoView>(R.id.video_dubbed)
        val cardOriginal = findViewById<androidx.cardview.widget.CardView>(R.id.card_original)
        val btnSplitView = findViewById<TextView>(R.id.btn_split_view)
        val btnToggleView = findViewById<TextView>(R.id.btn_toggle_view)

        // Load Videos
        projectId?.let { id ->
            val project = projectRepository.getProject(id)
            project?.videoUri?.let { uriString ->
                val uri = android.net.Uri.parse(uriString)
                videoOriginal.setVideoURI(uri)
                videoDubbed.setVideoURI(uri)
                
                // Play both
                videoOriginal.start()
                videoDubbed.start()
                
                // Sync loop
                videoOriginal.setOnPreparedListener { mp -> mp.isLooping = true }
                videoDubbed.setOnPreparedListener { mp -> mp.isLooping = true }
            }
        }

        btnSplitView.setOnClickListener {
            btnSplitView.setBackgroundResource(R.drawable.bg_toggle_active)
            btnSplitView.setTextColor(getColor(R.color.peony))
            btnSplitView.setTypeface(null, android.graphics.Typeface.BOLD)

            btnToggleView.setBackgroundResource(0)
            btnToggleView.setTextColor(getColor(R.color.onboarding_subtitle))
            btnToggleView.setTypeface(null, android.graphics.Typeface.NORMAL)

            cardOriginal.visibility = android.view.View.VISIBLE
        }

        btnToggleView.setOnClickListener {
            btnToggleView.setBackgroundResource(R.drawable.bg_toggle_active)
            btnToggleView.setTextColor(getColor(R.color.peony))
            btnToggleView.setTypeface(null, android.graphics.Typeface.BOLD)

            btnSplitView.setBackgroundResource(0)
            btnSplitView.setTextColor(getColor(R.color.onboarding_subtitle))
            btnSplitView.setTypeface(null, android.graphics.Typeface.NORMAL)

            cardOriginal.visibility = android.view.View.GONE
        }
    }
}
