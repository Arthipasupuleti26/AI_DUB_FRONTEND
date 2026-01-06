package com.simats.aidub

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.aidub.repository.ProjectRepository

class SelectVoiceActivity : AppCompatActivity() {

    private lateinit var projectRepository: ProjectRepository
    private var projectId: String? = null
    private var selectedVoice = "Arjun"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_voice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = ProjectRepository(this)
        projectId = intent.getStringExtra("PROJECT_ID")

        setupVoiceSelection()
        
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_continue).setOnClickListener {
            // Navigate to Voice Style
            val intent = Intent(this, VoiceStyleActivity::class.java)
            intent.putExtra("PROJECT_ID", projectId)
            intent.putExtra("SELECTED_VOICE", selectedVoice)
            startActivity(intent)
            finish()
        }
    }

    private fun setupVoiceSelection() {
        val cardArjun = findViewById<ConstraintLayout>(R.id.card_arjun)
        val cardSarah = findViewById<ConstraintLayout>(R.id.card_sarah)
        val cardRavi = findViewById<ConstraintLayout>(R.id.card_ravi)

        val cbArjun = findViewById<ImageView>(R.id.cb_arjun)
        val cbSarah = findViewById<ImageView>(R.id.cb_sarah)
        val cbRavi = findViewById<ImageView>(R.id.cb_ravi)

        fun updateSelection(selected: String) {
            selectedVoice = selected
            
            // Arjun
            if (selected == "Arjun") {
                cbArjun.setColorFilter(Color.parseColor("#A855F7"))
                cardArjun.backgroundTintList = null // Default white/drawable
            } else {
                cbArjun.setColorFilter(Color.parseColor("#D1D5DB"))
                cardArjun.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            }

            // Sarah
            if (selected == "Sarah") {
                cbSarah.setColorFilter(Color.parseColor("#A855F7"))
                cardSarah.backgroundTintList = null
            } else {
                cbSarah.setColorFilter(Color.parseColor("#D1D5DB"))
                cardSarah.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            }

            // Ravi
            if (selected == "Ravi") {
                cbRavi.setColorFilter(Color.parseColor("#A855F7"))
                cardRavi.backgroundTintList = null
            } else {
                cbRavi.setColorFilter(Color.parseColor("#D1D5DB"))
                cardRavi.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }

        cardArjun.setOnClickListener { updateSelection("Arjun") }
        cardSarah.setOnClickListener { updateSelection("Sarah") }
        cardRavi.setOnClickListener { updateSelection("Ravi") }
        
        // Initial state
        updateSelection("Arjun")
    }
}
