package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ExportSettingsActivity : AppCompatActivity() {

    private var selectedResolution = "1080p"
    private var selectedFormat = "MP4"
    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_export_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectId = intent.getStringExtra("PROJECT_ID")

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        setupResolutionSelection()
        setupFormatSelection()

        findViewById<AppCompatButton>(R.id.btn_export_video).setOnClickListener {
            val burnInSubtitles = findViewById<SwitchCompat>(R.id.switch_subtitles).isChecked
            
            // Persist settings
            projectId?.let { id ->
                val projectRepository = com.simats.aidub.repository.ProjectRepository(this)
                val project = projectRepository.getProject(id)
                project?.let {
                    val updatedProject = it.copy(
                        selectedResolution = selectedResolution,
                        selectedFormat = selectedFormat
                    )
                    projectRepository.updateProject(updatedProject)
                }
            }

            // Navigate to Rendering
            val intent = Intent(this, RenderingActivity::class.java)
            intent.putExtra("PROJECT_ID", projectId)
            startActivity(intent)
            finish()
        }
    }

    private fun setupResolutionSelection() {
        val chip720p = findViewById<TextView>(R.id.chip_720p)
        val chip1080p = findViewById<TextView>(R.id.chip_1080p)
        val chip4k = findViewById<TextView>(R.id.chip_4k)

        val chips = listOf(chip720p, chip1080p, chip4k)

        chips.forEach { chip ->
            chip.setOnClickListener {
                selectedResolution = chip.text.toString()
                updateChipStyles(chips, chip)
            }
        }
    }

    private fun setupFormatSelection() {
        val chipMp4 = findViewById<TextView>(R.id.chip_mp4)
        val chipMov = findViewById<TextView>(R.id.chip_mov)

        val chips = listOf(chipMp4, chipMov)

        chips.forEach { chip ->
            chip.setOnClickListener {
                selectedFormat = if (chip.text.contains("MP4")) "MP4" else "MOV"
                updateChipStyles(chips, chip)
            }
        }
    }

    private fun updateChipStyles(allChips: List<TextView>, selectedChip: TextView) {
        allChips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundResource(R.drawable.bg_option_card_selected)
                chip.setTextColor(ContextCompat.getColor(this, R.color.peony))
                chip.typeface = android.graphics.Typeface.DEFAULT_BOLD
            } else {
                chip.setBackgroundResource(R.drawable.bg_option_card)
                chip.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
                chip.typeface = android.graphics.Typeface.DEFAULT
            }
        }
    }
}
