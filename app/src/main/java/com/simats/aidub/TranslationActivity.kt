package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.aidub.model.TranslationResponse
import com.simats.aidub.network.ApiClient
import com.simats.aidub.repository.ProjectRepository
import com.simats.aidub.service.TranslationService
import android.view.View
import android.widget.Button

class TranslationActivity : AppCompatActivity() {

    private lateinit var projectRepository: ProjectRepository
    private lateinit var translationService: TranslationService
    private var projectId: String? = null
    private val translatedLines = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_translation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectRepository = ProjectRepository(this)
        translationService = TranslationService()
        projectId = intent.getStringExtra("PROJECT_ID")

        startActualTranslation()
    }

    private fun startActualTranslation() {

        val teluguText = projectRepository.getTranscribedText(projectId!!)

        val tvTelugu = findViewById<TextView>(R.id.tv_telugu_text)
        val tvEnglish = findViewById<TextView>(R.id.tv_english_text)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val btnNext = findViewById<Button>(R.id.btn_next)

        tvTelugu.text = teluguText
        progressBar.isIndeterminate = true

        ApiClient.apiService.translateText(projectId!!, teluguText)
            .enqueue(object : retrofit2.Callback<TranslationResponse> {

                override fun onResponse(
                    call: retrofit2.Call<TranslationResponse>,
                    response: retrofit2.Response<TranslationResponse>
                ) {
                    progressBar.isIndeterminate = false
                    progressBar.progress = 100

                    if (response.isSuccessful && response.body()?.success == true) {

                        val english = response.body()!!.english_text
                        tvEnglish.text = english

                        projectRepository.updateTranslatedText(projectId!!, english)
                        AppNotifier.notifySuccess(this@TranslationActivity,"Translation completed 🌍")

                        btnNext.visibility = View.VISIBLE
                        btnNext.setOnClickListener {
                            startActivity(
                                Intent(
                                    this@TranslationActivity,
                                    DetectEmotionsActivity::class.java
                                ).putExtra("PROJECT_ID", projectId)
                            )
                            finish()
                        }

                    } else {
                        Toast.makeText(this@TranslationActivity,"Translation failed",Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<TranslationResponse>, t: Throwable) {
                    Toast.makeText(this@TranslationActivity,t.message,Toast.LENGTH_LONG).show()
                }
            })
    }

}
