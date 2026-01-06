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

class UploadProgressActivity : AppCompatActivity() {
    
    private var isCancelled = false
    private var videoUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upload_progress)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get video URI from intent
        videoUri = intent.getStringExtra("VIDEO_URI")

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvPercentage = findViewById<TextView>(R.id.tv_percentage)
        
        // Cancel Button
        findViewById<TextView>(R.id.btn_cancel).setOnClickListener {
            isCancelled = true
            finish()
        }

        // Simulate Upload
        simulateUpload(progressBar, tvPercentage)
    }

    private fun simulateUpload(progressBar: ProgressBar, tvPercentage: TextView) {
        lifecycleScope.launch {
            if (!isNetworkAvailable()) {
                navigateToError()
                return@launch
            }

            for (i in 0..100) {
                if (isCancelled) break
                
                // Randomly check for network drops during simulation
                if (i > 10 && i % 20 == 0) {
                    if (!isNetworkAvailable()) {
                        navigateToError()
                        return@launch
                    }
                }

                progressBar.progress = i
                tvPercentage.text = "$i%"
                
                val delayTime = (30..80).random().toLong() 
                delay(delayTime)
            }

            if (!isCancelled) {
                delay(500) 
                val intent = Intent(this@UploadProgressActivity, UploadCompleteActivity::class.java)
                intent.putExtra("VIDEO_URI", videoUri)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun navigateToError() {
        startActivity(Intent(this, UploadFailedActivity::class.java))
        finish()
    }
}

