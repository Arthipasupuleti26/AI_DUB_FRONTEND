package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val session = getSharedPreferences("UserSession", MODE_PRIVATE)
        val isLoggedIn = session.getBoolean("is_logged_in", false)

        Handler(Looper.getMainLooper()).postDelayed({

            if (isLoggedIn) {
                // ✅ User already logged in → Home
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // ❌ Not logged in → Onboarding / Login
                startActivity(Intent(this, OnboardingActivity::class.java))
            }

            finish()

        }, 3000) // 3 seconds splash
    }
}
