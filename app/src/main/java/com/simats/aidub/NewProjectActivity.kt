package com.simats.aidub

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NewProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_project)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<ConstraintLayout>(R.id.layout_upload_area).setOnClickListener {
            android.content.Intent(this, SelectSourceActivity::class.java).also {
                startActivity(it)
            }
        }

        findViewById<LinearLayout>(R.id.btn_youtube_paste).setOnClickListener {
             android.content.Intent(this, PasteYoutubeLinkActivity::class.java).also {
                 startActivity(it)
             }
        }

        findViewById<LinearLayout>(R.id.btn_import_url).setOnClickListener {
             android.content.Intent(this, ImportUrlActivity::class.java).also {
                 startActivity(it)
             }
        }
    }
}
