package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Get User Data
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val name = sharedPref.getString("name", "User Name")
        val email = sharedPref.getString("email", "user@example.com")

        findViewById<TextView>(R.id.tv_user_name_profile).text = name
        findViewById<TextView>(R.id.tv_user_email_profile).text = email

        // Sign Out
        findViewById<LinearLayout>(R.id.btn_sign_out).setOnClickListener {
            // Clear Session (but keep DB)
            with(sharedPref.edit()) {
                clear()
                apply()
            }

            // Go to Login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Bottom Navigation
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0) // No animation
        }

        // Feature Toasts
        val toast = { msg: String -> android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show() }
        
        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<LinearLayout>(R.id.nav_settings).setOnClickListener {
             startActivity(Intent(this, SettingsActivity::class.java))
             overridePendingTransition(0, 0)
        }
        findViewById<LinearLayout>(R.id.btn_subscription_plan).setOnClickListener {
             startActivity(Intent(this, SubscriptionActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.btn_app_settings).setOnClickListener {
             startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.btn_account_details).setOnClickListener {
             startActivity(Intent(this, AccountDetailsActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.btn_help_support).setOnClickListener {
             startActivity(Intent(this, HelpSupportActivity::class.java))
        }

        // FAB Click
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add).setOnClickListener {
             startActivity(Intent(this, NewProjectActivity::class.java))
        }

    }
}
