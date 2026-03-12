package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.aidub.adapter.ProjectAdapter
import com.simats.aidub.repository.ProjectRepository

class MainActivity : AppCompatActivity() {
    
    private lateinit var projectRepository: ProjectRepository
    private lateinit var projectAdapter: ProjectAdapter
    private lateinit var emptyStateLayout: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var dashboardContent: android.widget.ScrollView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // Look out for bottom nav
            insets
        }

        // Initialize Repository
        projectRepository = ProjectRepository(this)

        // Get User Name from Mock Auth
        val sharedPref = getSharedPreferences("MockAuth", MODE_PRIVATE)
        val userName = sharedPref.getString("name", "Creator") ?: "Creator"

        findViewById<TextView>(R.id.tv_user_name).text = userName
        
        // Notifications Logic
        findViewById<android.widget.ImageView>(R.id.iv_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        // User Name & Profile Icon Click -> Profile
        findViewById<TextView>(R.id.tv_user_name).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<android.widget.ImageView>(R.id.iv_profile_header).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Setup views
        emptyStateLayout = findViewById(R.id.layout_empty_state)
        dashboardContent = findViewById(R.id.scroll_dashboard_content)
        
        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rv_recent_projects)
        projectAdapter = ProjectAdapter { project ->
            // Navigate based on project status and stage
            when {
                project.status == "Ready" -> {
                    // Project is complete - could open player/viewer
                    android.widget.Toast.makeText(this, "Opening: ${project.title}", android.widget.Toast.LENGTH_SHORT).show()
                }
                project.processingStage == "extracting_audio" -> {
                    // Resume audio extraction
                    val intent = Intent(this, ExtractAudioActivity::class.java)
                    intent.putExtra("VIDEO_URI", project.videoUri)
                    intent.putExtra("PROJECT_ID", project.id)
                    startActivity(intent)
                }
                project.processingStage == "transcribing" -> {
                    // Navigate to transcription screen
                    val intent = Intent(this, TranscriptionActivity::class.java)
                    intent.putExtra("PROJECT_ID", project.id)
                    startActivity(intent)
                }
                project.processingStage == "translating" -> {
                    // Navigate to translation screen
                    val intent = Intent(this, TranslationActivity::class.java)
                    intent.putExtra("PROJECT_ID", project.id)
                    startActivity(intent)
                }
                project.processingStage == "generating_voice" -> {
                    // Navigate to voice generation screen
                    val intent = Intent(this, VoiceGenerationActivity::class.java)
                    intent.putExtra("PROJECT_ID", project.id)
                    startActivity(intent)
                }
                else -> {
                    android.widget.Toast.makeText(this, "Opening: ${project.title}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = projectAdapter

        // FAB Click
        val fab = findViewById<android.view.View>(R.id.fab_add)
        fab.setOnClickListener {
             startActivity(Intent(this, NewProjectActivity::class.java))
        }
        
        // "Create New Project" Button in Empty State
        findViewById<android.widget.Button>(R.id.btn_create_first_project).setOnClickListener {
             startActivity(Intent(this, NewProjectActivity::class.java))
        }

        // Bottom Navigation - History Click
        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            overridePendingTransition(0, 0)
        }

        // Bottom Navigation - Profile Click
        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(0, 0)
        }

        // Bottom Navigation - Settings Click
        findViewById<LinearLayout>(R.id.nav_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadProjects()
    }
    
    private fun loadProjects() {
        val projects = projectRepository.getRecentProjects()
        
        if (projects.isNotEmpty()) {
            emptyStateLayout.visibility = android.view.View.GONE
            dashboardContent.visibility = android.view.View.VISIBLE
            projectAdapter.updateProjects(projects)
        } else {
            emptyStateLayout.visibility = android.view.View.VISIBLE
            dashboardContent.visibility = android.view.View.GONE
        }
    }
}