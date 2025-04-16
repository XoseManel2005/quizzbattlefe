package com.xose.quizzbattle.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import retrofit2.Call
import retrofit2.Response
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.model.LoginRequest
import com.xose.quizzbattle.model.User
import com.xose.quizzbattle.util.SessionManager

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnRegister)
        val tvRegisterIntent = findViewById<TextView>(R.id.tvGenericText)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        val text = getString(R.string.register_link)
        val spannable = SpannableString(text)
        spannable.setSpan(UnderlineSpan(), 0, text.length, 0)
        tvRegisterIntent.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        tvRegisterIntent.text = spannable

        tvRegisterIntent.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mostrar datos antes de enviarlos
            Log.d("LOGIN_DEBUG", "Usuario: $username")
            Log.d("LOGIN_DEBUG", "Contraseña: ${"*".repeat(password.length)}")

            val loginRequest = LoginRequest(username, password)
            val call = ApiClient.getClientService(this).login(loginRequest)
            val sessionManager = SessionManager(this@LoginActivity)

            call.enqueue(object : retrofit2.Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()

                        // Obtener token del header
                        val token = response.headers()["authorization"]?.removePrefix("Bearer ") ?: ""

                        sessionManager.saveAuthToken(token)
                        sessionManager.saveLoggedUser(user)

                        // Comprobación después de guardar
                        Log.d("LoginDebug", "Token guardado: ${sessionManager.getAuthToken()}")
                        Log.d("LoginDebug", "Usuario guardado: ${sessionManager.getLoggedUser().toString()}")

                        val intent = Intent(this@LoginActivity, GamesActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.d("LOGIN_ERROR","Error de red: ${t.message}")
                }
            })
        }
    }
}