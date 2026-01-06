package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back button -> Finish
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_reset_password).setOnClickListener {
            val email = findViewById<EditText>(R.id.et_reset_email).text.toString()
            val newPassword = findViewById<EditText>(R.id.et_new_password).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.et_confirm_password).text.toString()

            if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mock Reset: Update password in SharedPreferences DB
            val authDb = getSharedPreferences("MockAuthDB", MODE_PRIVATE)
            val userData = authDb.getString(email, null)

            if (userData != null) {
                val parts = userData.split("|")
                if (parts.isNotEmpty()) {
                    val name = parts[0]
                    with (authDb.edit()) {
                        putString(email, "$name|$newPassword")
                        apply()
                    }
                    Toast.makeText(this, "Password Reset Successful", Toast.LENGTH_SHORT).show()
                    finish() // Go back to login
                }
            } else {
                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
