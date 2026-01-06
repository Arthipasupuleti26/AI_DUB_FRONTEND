package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetectEmotionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detect_emotions)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        simulateDetection()
    }

    private fun simulateDetection() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvPercentage = findViewById<TextView>(R.id.tv_percentage)
        val tvStatus = findViewById<TextView>(R.id.tv_status)
        
        lifecycleScope.launch {
            for (i in 0..100) {
                progressBar.progress = i
                tvPercentage.text = "$i %"
                
                if (i < 30) {
                    tvStatus.text = "Analyzing vocal tones..."
                } else if (i < 70) {
                    tvStatus.text = "Identifying emotional patterns..."
                } else {
                    tvStatus.text = "Mapping emotional cues..."
                }

                // Simulate varying speeds
                val delayTime = if (i > 60 && i < 80) 80L else 30L
                delay(delayTime)
            }
            
            // Show Next Button
            val btnNext = findViewById<Button>(R.id.btn_next)
            btnNext.visibility = View.VISIBLE
            btnNext.setOnClickListener {
                 val intent = Intent(this@DetectEmotionsActivity, VoiceGenerationActivity::class.java)
                 // Pass any relevant data if needed
                 startActivity(intent)
                 finish()
            }
        }
    }
}
