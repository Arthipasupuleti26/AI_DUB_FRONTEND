package com.simats.aidub

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

class TranslationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_translation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        simulateTranslation()
    }

    private fun simulateTranslation() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvPercentage = findViewById<TextView>(R.id.tv_percentage)

        lifecycleScope.launch {
            for (i in 0..100) {
                progressBar.progress = i
                tvPercentage.text = "$i %"
                
                // Simulate varying speeds
                val delayTime = if (i > 80) 100L else 40L
                delay(delayTime)
            }
            
            // Show Next Button
            val btnNext = findViewById<android.widget.Button>(R.id.btn_next)
            btnNext.visibility = android.view.View.VISIBLE
            btnNext.setOnClickListener {
                 val intent = android.content.Intent(this@TranslationActivity, DetectEmotionsActivity::class.java)
                 startActivity(intent)
                 finish()
            }
        }
    }
}
