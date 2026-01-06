package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class VoiceControlsActivity : AppCompatActivity() {

    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voice_controls)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectId = intent.getStringExtra("PROJECT_ID")

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        setupSeekBars()

        findViewById<Button>(R.id.btn_generate_preview).setOnClickListener {
            // Navigate to Voice Preview
            val intent = Intent(this, VoicePreviewActivity::class.java)
            intent.putExtra("PROJECT_ID", projectId)
            startActivity(intent)
            finish()
        }
    }

    private fun setupSeekBars() {
        val seekSpeed = findViewById<SeekBar>(R.id.seek_speed)
        val tvSpeed = findViewById<TextView>(R.id.tv_val_speed)
        
        seekSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSpeed.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val seekPitch = findViewById<SeekBar>(R.id.seek_pitch)
        val tvPitch = findViewById<TextView>(R.id.tv_val_pitch)
        
        seekPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvPitch.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
