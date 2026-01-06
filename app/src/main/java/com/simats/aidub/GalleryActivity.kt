package com.simats.aidub

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gallery)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }
        
        // Mock item click
        val clickListener = {
            Toast.makeText(this, "Video Selected", Toast.LENGTH_SHORT).show()
            // In real app, finish with result
            finish()
        }
        
        findViewById<android.view.View>(R.id.item_video_1).setOnClickListener { clickListener() }
        findViewById<android.view.View>(R.id.item_video_2).setOnClickListener { clickListener() }
    }
}
