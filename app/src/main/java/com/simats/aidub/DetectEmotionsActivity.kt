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
import com.simats.aidub.model.EmotionResponse
import com.simats.aidub.network.ApiClient
import com.simats.aidub.repository.ProjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetectEmotionsActivity : AppCompatActivity() {

    private lateinit var projectRepository: ProjectRepository
    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detect_emotions)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = ProjectRepository(this)
        projectId = intent.getStringExtra("PROJECT_ID")

        simulateDetection()
    }

    private fun simulateDetection() {

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvPercentage = findViewById<TextView>(R.id.tv_percentage)
        val tvStatus = findViewById<TextView>(R.id.tv_status)

        val tagPrimary = findViewById<View>(R.id.tag_primary)
        val tvPrimary = findViewById<TextView>(R.id.tv_primary_emotion)
        val tagSecondary = findViewById<View>(R.id.tag_secondary)
        val tvSecondary = findViewById<TextView>(R.id.tv_secondary_emotion)

        val englishText = projectRepository.getProject(projectId!!)?.translatedText ?: ""

        tvStatus.text = "Analyzing vocal tones..."

        ApiClient.apiService.detectEmotion(projectId!!, englishText)
            .enqueue(object : retrofit2.Callback<EmotionResponse> {

                override fun onResponse(
                    call: retrofit2.Call<EmotionResponse>,
                    response: retrofit2.Response<EmotionResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        val emotions = mapOf(
                            "Happy" to (response.body()?.emotion_happiness ?: 0),
                            "Sad" to (response.body()?.emotion_sadness ?: 0),
                            "Angry" to (response.body()?.emotion_anger ?: 0),
                            "Calm" to (response.body()?.emotion_neutral ?: 0)
                        ).toList().sortedByDescending { it.second }

                        val primary = emotions[0]
                        val secondary = emotions[1]

                        progressBar.progress = 100
                        tvPercentage.text = "100%"
                        tvStatus.text = "Emotion detected"

                        tvPrimary.text = "${primary.first} ${primary.second}%"
                        tvSecondary.text = "${secondary.first} ${secondary.second}%"

                        tagPrimary.visibility = View.VISIBLE
                        tagSecondary.visibility = View.VISIBLE

                        projectRepository.updateDetectedEmotion(projectId!!, primary.first)
                        AppNotifier.notifySuccess(this@DetectEmotionsActivity,"Emotion detected: ${primary.first} 😊")

                        projectRepository.updateProjectProgress(projectId!!, "generating_voice", 0)

                        findViewById<Button>(R.id.btn_next).apply {
                            visibility = View.VISIBLE
                            setOnClickListener {
                                startActivity(
                                    Intent(
                                        this@DetectEmotionsActivity,
                                        SelectVoiceActivity::class.java
                                    ).putExtra("PROJECT_ID", projectId)
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<EmotionResponse>, t: Throwable) {
                    tvStatus.text = "Emotion detection failed"
                }
            })
    }

}
