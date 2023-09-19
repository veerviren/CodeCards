package com.example.scorecards.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.scorecards.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class Registration : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.RegistationEmail)
        val password = findViewById<EditText>(R.id.RegistationPassword)
        val confirmPassword = findViewById<EditText>(R.id.RegistationConfirmPassword)
        val registrationButton = findViewById<Button>(R.id.RegistationButton)

        registrationButton.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val confirmPasswordText = confirmPassword.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordText != confirmPasswordText) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                        // You can navigate to the next screen or perform other actions here
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "Invalid email or password format", Toast.LENGTH_SHORT).show()
                        } else if (exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "User with this email already exists", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Registration failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                            Log.d("Registration", "Registration failed: ${exception?.message}")
                        }
                    }
                }
        }
    }
}
