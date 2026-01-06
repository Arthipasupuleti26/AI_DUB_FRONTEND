package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Login button -> Home (MainActivity)
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            val email = findViewById<android.widget.EditText>(R.id.et_login_email).text.toString()
            val password = findViewById<android.widget.EditText>(R.id.et_login_password).text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Please fill in all fields", android.widget.Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                android.widget.Toast.makeText(this, "Password must be at least 6 characters", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                // Verify credentials against DB (Mock Auth Multi-User)
                val authDb = getSharedPreferences("MockAuthDB", MODE_PRIVATE)
                val userData = authDb.getString(email, null)

                if (userData != null) {
                    val parts = userData.split("|")
                    if (parts.size == 2 && parts[1] == password) {
                         // Login Success
                         val name = parts[0]
                         
                         // Save session for MainActivity
                         val session = getSharedPreferences("MockAuth", MODE_PRIVATE)
                         with(session.edit()) {
                             putString("name", name)
                             putString("email", email)
                             apply()
                         }

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                         android.widget.Toast.makeText(this, "Invalid email or password", android.widget.Toast.LENGTH_SHORT).show()
                    }
                } else {
                    android.widget.Toast.makeText(this, "User not found. Please sign up.", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Sign Up link -> CreateAccountActivity
        findViewById<TextView>(R.id.tv_signup_link).setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
            finish()
        }
        
        // Forgot password -> Navigate to ResetPasswordActivity
         findViewById<TextView>(R.id.tv_forgot_password).setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }
}
