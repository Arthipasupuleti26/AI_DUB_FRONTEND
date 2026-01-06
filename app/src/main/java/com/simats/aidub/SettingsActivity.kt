package com.simats.aidub

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Shared Preferences for Settings
        val sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE)
        
        // Dark Mode Logic
        val darkModeSwitch = findViewById<SwitchCompat>(R.id.switch_dark_mode)
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("dark_mode", isChecked)
                apply()
            }
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Push Notifications Logic
        val notifSwitch = findViewById<SwitchCompat>(R.id.switch_notifications)
        val isNotifEnabled = sharedPref.getBoolean("notifications", true)
        notifSwitch.isChecked = isNotifEnabled

        notifSwitch.setOnCheckedChangeListener { _, isChecked ->
             with(sharedPref.edit()) {
                putBoolean("notifications", isChecked)
                apply()
            }
             val msg = if (isChecked) "Notifications Enabled" else "Notifications Disabled"
             android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
        }

        // Privacy Policy
        findViewById<android.widget.LinearLayout>(R.id.btn_privacy_policy).setOnClickListener {
            startActivity(android.content.Intent(this, PrivacyPolicyActivity::class.java))
        }

        // Terms of Service
        findViewById<android.widget.LinearLayout>(R.id.btn_terms_of_service).setOnClickListener {
            startActivity(android.content.Intent(this, TermsOfServiceActivity::class.java))
        }

        // App Language
        findViewById<android.widget.LinearLayout>(R.id.btn_app_language).setOnClickListener {
            startActivity(android.content.Intent(this, LanguageActivity::class.java))
        }
    }
}
