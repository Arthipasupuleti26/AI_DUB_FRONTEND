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

class HistoryActivity : AppCompatActivity() {

    private lateinit var projectAdapter: ProjectAdapter
    private lateinit var projectRepository: ProjectRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = ProjectRepository(this)
        setupRecyclerView()
        setupBottomNavigation()
        setupClearHistory()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_history_projects)

        projectAdapter = ProjectAdapter(emptyList()) { project ->
            val intent = Intent(this, ProjectDetailsActivity::class.java)
            intent.putExtra("PROJECT_ID", project.id)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = projectAdapter

        loadProjects()
    }

    private fun loadProjects() {
        val projects = projectRepository.getRecentProjects()
        projectAdapter.updateProjects(projects)
    }

    private fun setupClearHistory() {
        findViewById<TextView>(R.id.btn_clear_history).setOnClickListener {

            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Clear History")
                .setMessage("Are you sure you want to delete all history?")
                .setPositiveButton("Yes") { _, _ ->
                    projectRepository.deleteAllProjects()
                    loadProjects()
                    android.widget.Toast.makeText(
                        this,
                        "History cleared",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.nav_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add)
            .setOnClickListener {
                startActivity(Intent(this, NewProjectActivity::class.java))
            }
    }
}
