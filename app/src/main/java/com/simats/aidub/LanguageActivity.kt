package com.simats.aidub

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LanguageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_language)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        val radioGroup = findViewById<RadioGroup>(R.id.radio_group_languages)
        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val currentLanguage = sharedPref.getString("language", "English")

        // Set initial state
        when (currentLanguage) {
            "English" -> findViewById<RadioButton>(R.id.rb_english).isChecked = true
            "Spanish" -> findViewById<RadioButton>(R.id.rb_spanish).isChecked = true
            "French" -> findViewById<RadioButton>(R.id.rb_french).isChecked = true
            "German" -> findViewById<RadioButton>(R.id.rb_german).isChecked = true
             "Chinese" -> findViewById<RadioButton>(R.id.rb_chinese).isChecked = true
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_english) {
                with(sharedPref.edit()) {
                    putString("language", "English")
                    apply()
                }
            } else {
                // Revert to English (visually or just warn)
                // For now, just showing the warning as requested
                // Ideally we might want to prevent selection or revert it
                
                // If we want to strictly enforce English for now:
                // findViewById<RadioButton>(R.id.rb_english).isChecked = true 
                // But the user just asked for a warning.
                
                Toast.makeText(this, "Warning: This language will be available in a future update", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
