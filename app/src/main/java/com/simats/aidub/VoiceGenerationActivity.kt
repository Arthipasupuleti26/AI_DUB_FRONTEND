package com.simats.aidub

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.simats.aidub.repository.ProjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.simats.aidub.network.ApiClient
import com.simats.aidub.model.GenericResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoiceGenerationActivity : AppCompatActivity() {

    private lateinit var projectRepository: ProjectRepository
    private var projectId: String? = null
    private var startProgress: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voice_generation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = ProjectRepository(this)
        projectId = intent.getStringExtra("PROJECT_ID")

        // Resume progress if exists
        projectId?.let { id ->
            val project = projectRepository.getProject(id)
            if (project != null && project.processingStage == "generating_voice") {
                startProgress = project.processingProgress
            }
        }
        
        startWaveformAnimation()
        simulateGeneration()
    }

    private fun startWaveformAnimation() {
        val bars = listOf(
            findViewById<View>(R.id.bar1),
            findViewById<View>(R.id.bar2),
            findViewById<View>(R.id.bar3),
            findViewById<View>(R.id.bar4),
            findViewById<View>(R.id.bar5)
        )

        bars.forEachIndexed { index, bar ->
            val animator = ObjectAnimator.ofFloat(bar, "scaleY", 0.3f, 1f, 0.3f)
            animator.duration = 600L + (index * 100L)
            animator.repeatCount = ValueAnimator.INFINITE
            animator.repeatMode = ValueAnimator.REVERSE
            animator.interpolator = LinearInterpolator()
            animator.startDelay = index * 50L
            animator.start()
        }
    }

    private fun simulateGeneration() {

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvPercentage = findViewById<TextView>(R.id.tv_percentage)
        val tvStatus = findViewById<TextView>(R.id.tv_status)

        val project = projectId?.let { projectRepository.getProject(it) }
        val voice = project?.selectedVoice ?: "Arjun"
        val style = project?.selectedStyle ?: "Professional"
        val text = project?.translatedText ?: ""

        if(text.isEmpty()){
            Toast.makeText(this,"No translated text found!",Toast.LENGTH_LONG).show()
            finish()
            return
        }

        tvStatus.text = "Connecting to AI voice engine..."
        progressBar.progress = 10
        tvPercentage.text = "10%"

        // 🔥 REAL BACKEND CALL
        ApiClient.apiService.generateVoice(
            projectId = projectId!!,
            text = text,
            voice = voice,
            style = style
        ).enqueue(object : Callback<GenericResponse> {

            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {

                if(response.isSuccessful && response.body()?.success == true){
                    AppNotifier.notifySuccess(this@VoiceGenerationActivity,"AI voice generated 🔊")

                    // Fake smooth finishing animation (backend already finished)
                    lifecycleScope.launch {
                        val messages = listOf(
                            "Synthesizing speech...",
                            "Applying emotion tuning...",
                            "Finalizing audio...",
                            "Almost done..."
                        )

                        var progress = 20
                        for (msg in messages) {
                            tvStatus.text = msg
                            val target = progress + 20

                            while (progress < target) {
                                progress += 2
                                progressBar.progress = progress
                                tvPercentage.text = "$progress%"
                                delay(80)
                            }
                        }


                        progressBar.progress = 100
                        tvPercentage.text = "100%"
                        tvStatus.text = "Voice generation complete!"

                        delay(500)
                        onGenerationComplete()
                    }

                }else{
                    Toast.makeText(this@VoiceGenerationActivity,"Voice generation failed",Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@VoiceGenerationActivity,t.message,Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }


    private fun onGenerationComplete() {
        Toast.makeText(this, "Voice generation complete!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, VoicePreviewActivity::class.java)
        intent.putExtra("PROJECT_ID", projectId)
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        projectId?.let { id ->
            projectRepository.updateProjectProgress(id, "generating_voice", progressBar.progress)
        }
        Toast.makeText(this, "Progress saved.", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
    }
}
