package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.simats.aidub.network.ApiClient
import java.util.Locale

class VoiceControlsActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var projectId: String? = null
    private lateinit var projectRepository: com.simats.aidub.repository.ProjectRepository
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voice_controls)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = com.simats.aidub.repository.ProjectRepository(this)
        projectId = intent.getStringExtra("PROJECT_ID")
        
        // Initialize TTS
        tts = TextToSpeech(this, this)

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        setupSeekBars()

        findViewById<androidx.cardview.widget.CardView>(R.id.card_preview).setOnClickListener {
            playTunedPreview()
        }

        findViewById<Button>(R.id.btn_generate_preview).setOnClickListener {

            val speedVal = findViewById<SeekBar>(R.id.seek_speed).progress / 50f
            val pitchVal = findViewById<SeekBar>(R.id.seek_pitch).progress / 50f

            val normalizedSpeed = if (speedVal <= 0f) 0.1f else speedVal
            val normalizedPitch = if (pitchVal <= 0f) 0.1f else pitchVal

            // save locally
            projectRepository.updateVoiceTuning(projectId!!, normalizedSpeed, normalizedPitch)

            // 🔥 SEND TO BACKEND
            ApiClient.apiService.saveVoiceTuning(
                projectId!!,
                normalizedSpeed,
                normalizedPitch
            ).enqueue(object : retrofit2.Callback<com.simats.aidub.model.GenericResponse> {

                override fun onResponse(
                    call: retrofit2.Call<com.simats.aidub.model.GenericResponse>,
                    response: retrofit2.Response<com.simats.aidub.model.GenericResponse>
                ) {
                    if(response.isSuccessful && response.body()?.success == true){

                        // ⭐ NOW show notification (REAL SUCCESS)
                        AppNotifier.notifySuccess(
                            this@VoiceControlsActivity,
                            "Voice tuning saved 🎤"
                        )

                        startActivity(
                            Intent(this@VoiceControlsActivity, VoiceGenerationActivity::class.java)
                                .putExtra("PROJECT_ID", projectId)
                        )
                    } else {
                        Toast.makeText(
                            this@VoiceControlsActivity,
                            "Failed to save tuning",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<com.simats.aidub.model.GenericResponse>, t: Throwable) {
                    Toast.makeText(this@VoiceControlsActivity,t.message,Toast.LENGTH_LONG).show()
                }
            })
        }


    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        }
    }

    private fun playTunedPreview() {
        val speedVal = findViewById<SeekBar>(R.id.seek_speed).progress / 50f
        val pitchVal = findViewById<SeekBar>(R.id.seek_pitch).progress / 50f
        
        val normalizedSpeed = if (speedVal <= 0f) 0.1f else speedVal
        val normalizedPitch = if (pitchVal <= 0f) 0.1f else pitchVal

        tts?.setPitch(normalizedPitch)
        tts?.setSpeechRate(normalizedSpeed)

        val project = projectId?.let { projectRepository.getProject(it) }
        val voice = project?.selectedVoice ?: "Arjun"
        val style = project?.selectedStyle ?: "Professional"
        
        val sampleText = "Hello! This is a preview of the $style voice for $voice. I am speaking at your adjusted speed and pitch."
        
        tts?.speak(sampleText, TextToSpeech.QUEUE_FLUSH, null, "PreviewID")
        
        android.widget.Toast.makeText(this, "🔊 Playing tuned preview for $voice...", android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    private fun setupSeekBars() {
        val seekSpeed = findViewById<SeekBar>(R.id.seek_speed)
        val tvSpeed = findViewById<TextView>(R.id.tv_val_speed)
        
        seekSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSpeed.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val seekPitch = findViewById<SeekBar>(R.id.seek_pitch)
        val tvPitch = findViewById<TextView>(R.id.tv_val_pitch)
        
        seekPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvPitch.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
