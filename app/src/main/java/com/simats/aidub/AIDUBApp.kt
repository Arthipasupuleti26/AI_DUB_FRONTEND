package com.simats.aidub

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class AIDUBApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Dark Mode based on user preference
        val sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE)
        
        // Initialize Notification Channel
        NotificationHelper(this).createNotificationChannel()
        
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)
        
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
