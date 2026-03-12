package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.aidub.network.ApiClient
import com.simats.aidub.repository.ProjectRepository

class EmotionIntensityActivity : AppCompatActivity() {

    private lateinit var projectRepository: ProjectRepository
    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_emotion_intensity)
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

        setupSeekBars()

        findViewById<Button>(R.id.btn_continue).setOnClickListener {

            val happiness = findViewById<SeekBar>(R.id.seek_happiness).progress
            val excitement = findViewById<SeekBar>(R.id.seek_excitement).progress
            val sadness = findViewById<SeekBar>(R.id.seek_sadness).progress

            projectRepository.updateEmotionIntensity(projectId!!, happiness, excitement, sadness)

            // 🔥 SEND TO BACKEND
            ApiClient.apiService.saveEmotion(
                projectId!!,
                happiness,
                excitement,
                sadness
            ).enqueue(object : retrofit2.Callback<com.simats.aidub.model.GenericResponse> {

                override fun onResponse(
                    call: retrofit2.Call<com.simats.aidub.model.GenericResponse>,
                    response: retrofit2.Response<com.simats.aidub.model.GenericResponse>
                ) {
                    if(response.isSuccessful && response.body()?.success == true){
                        AppNotifier.notifySuccess(this@EmotionIntensityActivity,"Emotion tuning saved 🎚")

                        startActivity(
                            Intent(this@EmotionIntensityActivity, VoiceControlsActivity::class.java)
                                .putExtra("PROJECT_ID", projectId)
                        )
                    } else {
                        Toast.makeText(this@EmotionIntensityActivity,"Failed to save emotion",Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<com.simats.aidub.model.GenericResponse>, t: Throwable) {
                    Toast.makeText(this@EmotionIntensityActivity,t.message,Toast.LENGTH_LONG).show()
                }
            })
        }

    }

    private fun setupSeekBars() {
        val seekHappiness = findViewById<SeekBar>(R.id.seek_happiness)
        val tvHappiness = findViewById<TextView>(R.id.tv_val_happiness)
        
        seekHappiness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvHappiness.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val seekExcitement = findViewById<SeekBar>(R.id.seek_excitement)
        val tvExcitement = findViewById<TextView>(R.id.tv_val_excitement)
        
        seekExcitement.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvExcitement.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val seekSadness = findViewById<SeekBar>(R.id.seek_sadness)
        val tvSadness = findViewById<TextView>(R.id.tv_val_sadness)
        
        seekSadness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSadness.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
