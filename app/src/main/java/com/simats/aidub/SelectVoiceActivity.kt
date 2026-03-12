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
import com.simats.aidub.model.GenericResponse
import com.simats.aidub.network.ApiClient
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

            val project = projectRepository.getProject(projectId!!)
            val englishText = project?.translatedText ?: ""

            if (englishText.isEmpty()) {
                Toast.makeText(this, "No translated text found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save selected voice locally
            projectRepository.updateSelectedVoice(projectId!!, selectedVoice)

            Toast.makeText(this, "Generating AI Voice...", Toast.LENGTH_SHORT).show()

            ApiClient.apiService.generateVoice(
                projectId = projectId!!,
                text = englishText,
                voice = selectedVoice,
                style = "Professional" // default style for now
            ).enqueue(object : retrofit2.Callback<GenericResponse> {

                override fun onResponse(
                    call: retrofit2.Call<GenericResponse>,
                    response: retrofit2.Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        Toast.makeText(
                            this@SelectVoiceActivity,
                            "Voice Generated 🎉",
                            Toast.LENGTH_LONG
                        ).show()

                        // Move to next screen
                        startActivity(
                            Intent(this@SelectVoiceActivity, VoiceStyleActivity::class.java)
                                .putExtra("PROJECT_ID", projectId)
                        )

                    } else {
                        Toast.makeText(
                            this@SelectVoiceActivity,
                            "Voice generation failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(
                        this@SelectVoiceActivity,
                        "Server error: " + t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
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
