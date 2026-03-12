package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.aidub.model.LoginResponse
import com.simats.aidub.network.ApiClient
import com.simats.aidub.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val etEmail = findViewById<EditText>(R.id.et_login_email)
        val etPassword = findViewById<EditText>(R.id.et_login_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        val apiService = ApiClient.apiService

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf(
                "email" to email,
                "password" to password
            )

            apiService.login(body).enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val res = response.body()!!
                        val user = res.user

                        val session = getSharedPreferences("UserSession", MODE_PRIVATE)
                        with(session.edit()) {
                            putBoolean("is_logged_in", true)   // ✅ ADD THIS
                            putString("user_id", user.id)
                            putString("name", user.name)
                            putString("email", user.email)
                            putString("token", user.token)
                            putString("session_id", res.session_id)
                            apply()
                        }


                        Toast.makeText(
                            this@LoginActivity,
                            res.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Server error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        findViewById<TextView>(R.id.tv_signup_link).setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.tv_forgot_password).setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }
}
