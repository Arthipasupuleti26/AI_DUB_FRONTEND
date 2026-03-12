package com.simats.aidub

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.aidub.repository.ProjectRepository

class VoicePreviewActivity : AppCompatActivity() {

    private lateinit var projectRepository: ProjectRepository
    private var projectId: String? = null
    private var mediaPlayer: MediaPlayer? = null

    // ⭐ YOUR SERVER AUDIO URL
    private val AUDIO_URL =
        "https://1j7cp4fh-80.inc1.devtunnels.ms/ai_dub/api/audio/outputs/voice/output.mp3"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voice_preview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = ProjectRepository(this)
        projectId = intent.getStringExtra("PROJECT_ID")

        findViewById<ImageView>(R.id.btn_back).setOnClickListener { finish() }

        // Show selected voice info
        projectId?.let { id ->
            val project = projectRepository.getProject(id)
            project?.let { p ->
                val voiceName = p.selectedVoice ?: "Arjun"
                val voiceStyle = p.selectedStyle ?: "Professional"
                val emotion = p.detectedEmotion ?: "Natural"
                findViewById<TextView>(R.id.tv_voice_type).text = "English Dub (AI)"
                findViewById<TextView>(R.id.tv_voice_details).text =
                    "$voiceName • $voiceStyle • $emotion"
            }
        }

        findViewById<ImageView>(R.id.btn_play).setOnClickListener {
            playFinalVoice()
        }

        findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            projectId?.let { id -> projectRepository.markProjectComplete(id) }
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()

            startActivity(
                Intent(this, VideoExportActivity::class.java)
                    .putExtra("PROJECT_ID", projectId)
            )
            finish()
        }

        findViewById<Button>(R.id.btn_regenerate).setOnClickListener {
            showRegenerateDialog()
        }
    }

    // ⭐ PLAY REAL AI VOICE FROM SERVER
    private fun playFinalVoice() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(AUDIO_URL)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    Toast.makeText(
                        this@VoicePreviewActivity,
                        "🔊 Playing generated AI voice...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                setOnErrorListener { _, _, _ ->
                    Toast.makeText(
                        this@VoicePreviewActivity,
                        "Failed to play audio",
                        Toast.LENGTH_LONG
                    ).show()
                    true
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    private fun showRegenerateDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_regenerate, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<Button>(R.id.btn_confirm_regenerate).setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Regenerating...", Toast.LENGTH_SHORT).show()

            startActivity(
                Intent(this, VoiceGenerationActivity::class.java)
                    .putExtra("PROJECT_ID", projectId)
            )
            finish()
        }

        dialogView.findViewById<TextView>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
