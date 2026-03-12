package com.simats.aidub

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditSubtitlesActivity : AppCompatActivity() {

    private lateinit var projectRepository: com.simats.aidub.repository.ProjectRepository
    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_subtitles)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = com.simats.aidub.repository.ProjectRepository(this)
        projectId = intent.getStringExtra("PROJECT_ID")

        val etSubtitles = findViewById<android.widget.EditText>(R.id.et_subtitles)

        // Load existing translated text
        projectId?.let { id ->
            val project = projectRepository.getProject(id)
            etSubtitles.setText(project?.translatedText)
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_done).setOnClickListener {
            projectId?.let { id ->
                val updatedText = etSubtitles.text.toString()
                projectRepository.updateTranslatedText(id, updatedText)
                Toast.makeText(this, "Subtitles saved successfully!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}
