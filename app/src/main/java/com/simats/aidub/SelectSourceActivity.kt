package com.simats.aidub

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SelectSourceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_source)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Common function to navigate to UploadProgressActivity directly
        fun startDummyUpload() {
            android.content.Intent(this, UploadProgressActivity::class.java).also {
                it.putExtra("VIDEO_URI", "content://com.simats.aidub/dummy_video.mp4")
                startActivity(it)
            }
        }

        // Gallery: Pick Video -> Now Direct Upload
        findViewById<ConstraintLayout>(R.id.btn_gallery).setOnClickListener {
             startDummyUpload()
        }

        // Camera: Record Video -> Now Direct Upload
        findViewById<ConstraintLayout>(R.id.btn_camera).setOnClickListener {
             startDummyUpload()
        }

        // Drive / File Picker: Import from Cloud -> Now Direct Upload
        findViewById<ConstraintLayout>(R.id.btn_drive).setOnClickListener {
             startDummyUpload()
        }
    }
}
