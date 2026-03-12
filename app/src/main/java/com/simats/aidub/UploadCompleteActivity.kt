package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UploadCompleteActivity : AppCompatActivity() {
    
    private var videoUri: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upload_complete)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get video URI from intent
        val backendVideoPath = intent.getStringExtra("VIDEO_SERVER_PATH")

        findViewById<Button>(R.id.btn_continue).setOnClickListener {
            val intent = Intent(this, ProjectDetailsActivity::class.java)
            intent.putExtra("VIDEO_SERVER_PATH", backendVideoPath)
            startActivity(intent)
        }
    }
}

