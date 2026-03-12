package com.simats.aidub

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.aidub.model.RegisterResponse
import com.simats.aidub.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        findViewById<Button>(R.id.btn_sign_up).setOnClickListener {

            val name = findViewById<EditText>(R.id.et_name).text.toString().trim()
            val email = findViewById<EditText>(R.id.et_email).text.toString().trim()
            val password = findViewById<EditText>(R.id.et_password).text.toString()

            // 🔹 Name validation (letters only)
            if (!name.matches(Regex("^[a-zA-Z ]+$"))) {
                Toast.makeText(this, "Name should contain only letters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Email validation
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Password validation
            val passwordRegex =
                Regex("^(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$")

            if (!passwordRegex.matches(password)) {
                Toast.makeText(
                    this,
                    "Password must be 8+ chars with 1 number & 1 symbol",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val body = mapOf(
                "name" to name,
                "email" to email,
                "password" to password
            )

            ApiClient.apiService.register(body)
                .enqueue(object : Callback<RegisterResponse> {

                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.status == "success") {

                            Toast.makeText(
                                this@CreateAccountActivity,
                                "Registered Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(
                                    this@CreateAccountActivity,
                                    LoginActivity::class.java
                                )
                            )
                            finish()

                        } else {
                            Toast.makeText(
                                this@CreateAccountActivity,
                                response.body()?.message ?: "Registration failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Toast.makeText(
                            this@CreateAccountActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        findViewById<TextView>(R.id.tv_login_link).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
