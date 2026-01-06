package com.simats.aidub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.aidub.model.Project
import com.simats.aidub.repository.ProjectRepository

class ProjectDetailsActivity : AppCompatActivity() {
    
    private var videoUri: String? = null
    private lateinit var projectRepository: ProjectRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_project_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Repository
        projectRepository = ProjectRepository(this)

        // Get video URI from intent
        videoUri = intent.getStringExtra("VIDEO_URI")
        
        // Setup VideoView
        val videoView = findViewById<VideoView>(R.id.video_view)
        if (videoUri != null) {
            val uri = Uri.parse(videoUri)
            videoView.setVideoURI(uri)
            
            // Add media controller for play/pause controls
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)
            
            // Start playing when ready
            videoView.setOnPreparedListener { mp ->
                // Show first frame as thumbnail
                videoView.seekTo(1)
            }
            
            videoView.setOnErrorListener { _, _, _ ->
                Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show()
                true
            }
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<LinearLayout>(R.id.btn_select_original_lang).setOnClickListener {
            Toast.makeText(this, "Original Language: Telugu (Fixed)", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.btn_select_target_lang).setOnClickListener {
            Toast.makeText(this, "Select Target Language", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_create_project).setOnClickListener {
            saveProject()
        }
    }
    
    private fun saveProject() {
        // Get input values
        val titleInput = findViewById<EditText>(R.id.et_project_title)
        val descriptionInput = findViewById<EditText>(R.id.et_description)
        val originalLangText = findViewById<TextView>(R.id.tv_original_lang)
        val targetLangText = findViewById<TextView>(R.id.tv_target_lang)
        
        val title = titleInput.text.toString().ifBlank { "Untitled Project" }
        val description = descriptionInput.text.toString()
        val originalLang = originalLangText.text.toString()
        val targetLang = targetLangText.text.toString()
        
        // Create Project object
        val project = Project(
            id = System.currentTimeMillis().toString(),
            title = title,
            description = description,
            videoUri = videoUri ?: "",
            originalLanguage = originalLang,
            targetLanguage = targetLang,
            status = "Processing",
            createdAt = System.currentTimeMillis()
        )
        
        // Save to repository
        projectRepository.saveProject(project)
        
        Toast.makeText(this, "Project Created! Extracting audio...", Toast.LENGTH_SHORT).show()
        
        // Navigate to Audio Extraction screen
        val intent = Intent(this, ExtractAudioActivity::class.java)
        intent.putExtra("VIDEO_URI", videoUri)
        intent.putExtra("PROJECT_ID", project.id)
        startActivity(intent)
        finish()
    }
}
