package com.simats.aidub

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_confirm).setOnClickListener {
             projectId?.let { id ->
                projectRepository.markProjectComplete(id)
            }
            Toast.makeText(this, "Project Completed Successfully!", Toast.LENGTH_SHORT).show()
            
            // Navigate to Main Activity / Dashboard
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btn_regenerate).setOnClickListener {
            showRegenerateDialog()
        }
    }

    private fun showRegenerateDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_regenerate, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
        
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<Button>(R.id.btn_confirm_regenerate).setOnClickListener {
            dialog.dismiss()
            // Navigate to Generating Voice page to restart generation
            Toast.makeText(this, "Regenerating...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, VoiceGenerationActivity::class.java)
            intent.putExtra("PROJECT_ID", projectId)
            // Clear stack to prevent back-nav weirdness? Or just start simple.
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Maybe not, we want to go back.
            startActivity(intent)
            finish() // Close Preview so we don't return to it immediately
        }

        dialogView.findViewById<TextView>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
