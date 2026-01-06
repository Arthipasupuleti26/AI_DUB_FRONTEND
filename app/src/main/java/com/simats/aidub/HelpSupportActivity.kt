package com.simats.aidub

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HelpSupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_help_support)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<android.widget.LinearLayout>(R.id.btn_contact_us).setOnClickListener {
            startActivity(android.content.Intent(this, ContactUsActivity::class.java))
        }

        findViewById<android.widget.LinearLayout>(R.id.btn_faq).setOnClickListener {
            startActivity(android.content.Intent(this, FaqActivity::class.java))
        }

        findViewById<android.widget.LinearLayout>(R.id.btn_send_feedback).setOnClickListener {
            startActivity(android.content.Intent(this, SendFeedbackActivity::class.java))
        }
    }
}
