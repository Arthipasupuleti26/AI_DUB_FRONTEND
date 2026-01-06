package com.simats.aidub

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AccountDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Get User Data
        val sharedPref = getSharedPreferences("MockAuth", MODE_PRIVATE)
        val name = sharedPref.getString("name", "User Name")
        val email = sharedPref.getString("email", "user@example.com")

        findViewById<android.widget.TextView>(R.id.tv_account_name).text = name
        findViewById<android.widget.TextView>(R.id.tv_account_email).text = email
        // Member Since is static for now as it's not stored in MockAuth
    }
}
