package com.simats.aidub

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
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

        val notificationHelper = NotificationHelper(this)

        val requestPermissionLauncher = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                notificationHelper.showNotification("Notifications Enabled", "You will now receive updates about your projects.")
            } else {
                notifSwitch.isChecked = false
                with(sharedPref.edit()) {
                    putBoolean("notifications", false)
                    apply()
                }
                android.widget.Toast.makeText(this, "Permission Denied", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        notifSwitch.setOnCheckedChangeListener { _, isChecked ->
             with(sharedPref.edit()) {
                putBoolean("notifications", isChecked)
                apply()
            }
            
            if (isChecked) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    if (androidx.core.content.ContextCompat.checkSelfPermission(
                            this, android.Manifest.permission.POST_NOTIFICATIONS
                        ) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        notificationHelper.showNotification("Notifications Enabled", "You will now receive updates about your projects.")
                    }
                } else {
                    notificationHelper.showNotification("Notifications Enabled", "You will now receive updates about your projects.")
                }
            } else {
                android.widget.Toast.makeText(this, "Notifications Disabled", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // Privacy Policy
        findViewById<android.widget.LinearLayout>(R.id.btn_privacy_policy).setOnClickListener {
            startActivity(android.content.Intent(this, PrivacyPolicyActivity::class.java))
        }

        // Terms of Service
        findViewById<android.widget.LinearLayout>(R.id.btn_terms_of_service).setOnClickListener {
            startActivity(android.content.Intent(this, TermsOfServiceActivity::class.java))
        }

        // Create Account navigation is usually triggered by buttons, but for completeness
        // we'll leave it as is for now.

        // Bottom Navigation
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            startActivity(android.content.Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            startActivity(android.content.Intent(this, ProfileActivity::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            startActivity(android.content.Intent(this, HistoryActivity::class.java))
            overridePendingTransition(0, 0)
        }
        
        // FAB Click
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add).setOnClickListener {
             startActivity(android.content.Intent(this, NewProjectActivity::class.java))
        }
    }
}
