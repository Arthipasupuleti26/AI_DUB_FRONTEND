package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RenderingActivity : AppCompatActivity() {

    private var projectId: String? = null
    private val totalFrames = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rendering)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectId = intent.getStringExtra("PROJECT_ID")
        simulateRendering()
    }

    private fun simulateRendering() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvPercentage = findViewById<TextView>(R.id.tv_percentage)
        val tvFrameCount = findViewById<TextView>(R.id.tv_frame_count)

        lifecycleScope.launch {
            for (i in 0..100) {
                progressBar.progress = i
                tvPercentage.text = "$i %"
                
                val currentFrame = (i * totalFrames) / 100
                tvFrameCount.text = "Rendering frame $currentFrame of $totalFrames"

                val delayTime = (30..80).random().toLong()
                delay(delayTime)
            }

            // Rendering complete
            delay(500)
            onRenderingComplete()
        }
    }

    private fun onRenderingComplete() {
        // Navigate to Success Screen
        val intent = Intent(this, ExportSuccessActivity::class.java)
        intent.putExtra("PROJECT_ID", projectId)
        startActivity(intent)
        finish()
    }
}
