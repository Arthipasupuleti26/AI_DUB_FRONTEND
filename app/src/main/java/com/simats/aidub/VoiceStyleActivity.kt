package com.simats.aidub

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class VoiceStyleActivity : AppCompatActivity() {

    private var projectId: String? = null
    private var selectedStyle = "Professional"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voice_style)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectId = intent.getStringExtra("PROJECT_ID")

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        setupStyleSelection()

        findViewById<Button>(R.id.btn_apply).setOnClickListener {
            // Navigate to Emotion Intensity
            val intent = Intent(this, EmotionIntensityActivity::class.java)
            intent.putExtra("PROJECT_ID", projectId)
            intent.putExtra("SELECTED_STYLE", selectedStyle)
            startActivity(intent)
            finish()
        }
    }

    private fun setupStyleSelection() {
        val cardProfessional = findViewById<CardView>(R.id.card_professional)
        val cardCasual = findViewById<CardView>(R.id.card_casual)
        val cardEnergetic = findViewById<CardView>(R.id.card_energetic)
        val cardCalm = findViewById<CardView>(R.id.card_calm)

        val iconProfessional = findViewById<ImageView>(R.id.icon_professional)
        val iconCasual = findViewById<ImageView>(R.id.icon_casual)
        val iconEnergetic = findViewById<ImageView>(R.id.icon_energetic)
        val iconCalm = findViewById<ImageView>(R.id.icon_calm)

        fun updateSelection(style: String) {
            selectedStyle = style
            
            // Helper to reset all
            val cards = listOf(cardProfessional, cardCasual, cardEnergetic, cardCalm)
            val icons = listOf(iconProfessional, iconCasual, iconEnergetic, iconCalm)
            
            cards.forEach { it.setCardBackgroundColor(Color.WHITE) }
            icons.forEach { 
                it.setColorFilter(Color.parseColor("#6B7280"))
                it.background.setTint(Color.parseColor("#F3F4F6"))
            }

            // Set selected
            val (selectedCard, selectedIcon) = when(style) {
                "Professional" -> Pair(cardProfessional, iconProfessional)
                "Casual" -> Pair(cardCasual, iconCasual)
                "Energetic" -> Pair(cardEnergetic, iconEnergetic)
                "Calm" -> Pair(cardCalm, iconCalm)
                else -> Pair(cardProfessional, iconProfessional)
            }

            selectedCard.setCardBackgroundColor(Color.parseColor("#F3F0FF"))
            selectedIcon.setColorFilter(Color.parseColor("#A855F7"))
            selectedIcon.background.setTint(Color.parseColor("#E9D5FF"))
        }

        cardProfessional.setOnClickListener { updateSelection("Professional") }
        cardCasual.setOnClickListener { updateSelection("Casual") }
        cardEnergetic.setOnClickListener { updateSelection("Energetic") }
        cardCalm.setOnClickListener { updateSelection("Calm") }

        // Initial
        updateSelection("Professional")
    }
}
