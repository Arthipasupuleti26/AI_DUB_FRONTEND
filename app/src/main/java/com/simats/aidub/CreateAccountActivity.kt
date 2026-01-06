package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sign Up button -> Navigate to Home (MainActivity)
        findViewById<Button>(R.id.btn_sign_up).setOnClickListener {
            val name = findViewById<android.widget.EditText>(R.id.et_name).text.toString()
            val email = findViewById<android.widget.EditText>(R.id.et_email).text.toString()
            val password = findViewById<android.widget.EditText>(R.id.et_password).text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Please fill in all fields", android.widget.Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                android.widget.Toast.makeText(this, "Password must be at least 6 characters", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                // Save credentials (Mock Auth)
                // Check for duplicate user (Mock Auth Multi-User)
                val authDb = getSharedPreferences("MockAuthDB", MODE_PRIVATE)
                if (authDb.contains(email)) {
                    android.widget.Toast.makeText(this, "Account with this email already exists", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    // Save to DB (email -> name|password)
                    with(authDb.edit()) {
                        putString(email, "$name|$password")
                        apply()
                    }

                    // Save session (current user)
                    val session = getSharedPreferences("MockAuth", MODE_PRIVATE)
                    with (session.edit()) {
                        putString("name", name)
                        putString("email", email)
                        apply()
                    }
                    
                    android.widget.Toast.makeText(this, "Account Created", android.widget.Toast.LENGTH_SHORT).show()

                    // User requested Sign Up to go to Login page
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }

        // Log in text -> Navigate to LoginActivity
        findViewById<TextView>(R.id.tv_login_link).setOnClickListener {
             startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
