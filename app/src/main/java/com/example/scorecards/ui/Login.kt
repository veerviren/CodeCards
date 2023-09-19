package com.example.scorecards.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.scorecards.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE)

        val email = findViewById<EditText>(R.id.LoginEmail)
        val password = findViewById<EditText>(R.id.LoginPassword)
        val LoginButton = findViewById<Button>(R.id.LoginButton)
        val CreateAccountButton = findViewById<Button>(R.id.CreateAccountButton)

        CreateAccountButton.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }

        // Check if email and password are available in local storage
        val savedEmail = sharedPreferences.getString("EMAIL", "")
        val savedPassword = sharedPreferences.getString("PASSWORD", "")

        if (savedEmail != "" && savedPassword != "") {
            email.setText(savedEmail)
            password.setText(savedPassword)
            if (savedEmail != null && savedPassword != null) {
                attemptLogin(savedEmail, savedPassword)
            }
        }

        LoginButton.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            attemptLogin(emailText, passwordText)
        }
    }

    private fun attemptLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    saveCredentials(email, password)

                    val intent = Intent(this, InputeCard::class.java)
                    startActivity(intent)
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid email or password format", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "${exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.d("Login", "Login failed: ${exception?.message}")
                    }
                }
            }
    }

    private fun saveCredentials(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("EMAIL", email)
        editor.putString("PASSWORD", password)
        editor.apply()
    }
}