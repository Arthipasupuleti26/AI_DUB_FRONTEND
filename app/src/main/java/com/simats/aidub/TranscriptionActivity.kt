package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.aidub.model.TranscriptionResponse
import com.simats.aidub.network.ApiClient
import com.simats.aidub.repository.ProjectRepository

class TranscriptionActivity : AppCompatActivity() {

    private lateinit var projectRepository: ProjectRepository
    private var projectId: String? = null
    private var startProgress: Int = 0

    private val statusMessages = listOf(
        "Loading audio data...",
        "Identifying speakers...",
        "Converting speech to text...",
        "Formatting subtitles...",
        "Finalizing transcription..."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transcription)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = ProjectRepository(this)
        projectId = intent.getStringExtra("PROJECT_ID")

        projectId?.let { id ->
            val project = projectRepository.getProject(id)
            if (project != null && project.processingStage == "transcribing") {
                startProgress = project.processingProgress
            }
        }

        startRealTranscription()
    }

    private fun startRealTranscription() {

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvStatus = findViewById<TextView>(R.id.tv_status)
        val tvPercentage = findViewById<TextView>(R.id.tv_percentage)

        tvStatus.text = statusMessages[2]
        progressBar.isIndeterminate = true

        val project = projectRepository.getProject(projectId!!)
        val audioPath = project?.audioPath ?: ""

        if (audioPath.isEmpty()) {
            Toast.makeText(this, "Audio not found", Toast.LENGTH_LONG).show()
            return
        }

        ApiClient.apiService.transcribeAudio(projectId!!, audioPath)
            .enqueue(object : retrofit2.Callback<TranscriptionResponse> {

                override fun onResponse(
                    call: retrofit2.Call<TranscriptionResponse>,
                    response: retrofit2.Response<TranscriptionResponse>
                ) {
                    progressBar.isIndeterminate = false
                    progressBar.progress = 100
                    tvPercentage.text = "100%"
                    tvStatus.text = statusMessages[4]

                    if (response.isSuccessful && response.body()?.success == true) {

                        val teluguText = response.body()?.text ?: ""

                        // ✅ Save transcription
                        projectRepository.updateTranscribedText(projectId!!, teluguText)
                        AppNotifier.notifySuccess(this@TranscriptionActivity,"Speech converted to text ✍️")
                        projectRepository.updateProjectProgress(projectId!!, "transcribing", 100)

                        // ✅ SHOW TELUGU SUBTITLES HERE
                        showTeluguSubtitles(teluguText)

                    } else {
                        Toast.makeText(
                            this@TranscriptionActivity,
                            "Transcription failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<TranscriptionResponse>,
                    t: Throwable
                ) {
                    progressBar.isIndeterminate = false
                    Toast.makeText(
                        this@TranscriptionActivity,
                        t.message ?: "Network error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    /**
     * ✅ This uses EXISTING XML:
     * scroll_text
     * text_container
     * btn_next
     */
    private fun showTeluguSubtitles(text: String) {

        val scrollView = findViewById<android.widget.ScrollView>(R.id.scroll_text)
        val container = findViewById<android.widget.LinearLayout>(R.id.text_container)
        val btnNext = findViewById<Button>(R.id.btn_next)

        container.removeAllViews()

        // Split into subtitle-style lines
        val lines = text
            .replace("।", ".")
            .split(".", "\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        for (line in lines) {
            val tv = TextView(this)
            tv.text = line
            tv.textSize = 16f
            tv.setTextColor(resources.getColor(android.R.color.black, theme))
            tv.setTypeface(null, android.graphics.Typeface.BOLD)
            tv.setPadding(0, 0, 0, 32)

            container.addView(tv)

            tv.alpha = 0f
            tv.animate().alpha(1f).setDuration(250).start()

            scrollView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        // ✅ Now allow user to continue
        btnNext.visibility = View.VISIBLE
        btnNext.setOnClickListener {
            startActivity(
                Intent(this@TranscriptionActivity, TranslationActivity::class.java)
                    .putExtra("PROJECT_ID", projectId)
            )
        }
    }

    override fun onBackPressed() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        projectId?.let { id ->
            projectRepository.updateProjectProgress(id, "transcribing", progressBar.progress)
        }
        Toast.makeText(this, "Progress saved.", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
    }
}
