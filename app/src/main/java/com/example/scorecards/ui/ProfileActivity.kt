package com.example.scorecards.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.scorecards.R
import com.example.scorecards.utils.canLegendaryGrandmaster
import com.example.scorecards.utils.setTextColorBasedOnRating
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val editUsernameButton = findViewById<Button>(R.id.editUsernameButton)
        val logOutButton = findViewById<Button>(R.id.logoutButton)
        val userName = findViewById<TextView>(R.id.profileUserName)
        val sharedPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE)

        val profileImageUrl = intent.getStringExtra("profileImage")
        Log.d("ProfileActivity", profileImageUrl.toString())

        Glide.with(this)
            .load(profileImageUrl)
            .placeholder(R.drawable.loading_effect)
            .error(R.drawable.error)
            .into(profileImage)

        val rating = intent.getIntExtra("rating", 0)

        userName.text = intent.getStringExtra("handle")

        userName.setTextColorBasedOnRating(rating)
        userName.canLegendaryGrandmaster(rating, userName)

        editUsernameButton.setOnClickListener {

            // delete locally saved handle
            val editor = sharedPreferences.edit()
            editor.putString("HANDLE", "")
            editor.apply()

            intent = Intent(this, InputeCard::class.java)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {

            // delete locally saved user info
            val editor = sharedPreferences.edit()
            editor.putString("EMAIL", "")
            editor.putString("PASSWORD", "")
            editor.putString("HANDLE", "")
            editor.apply()
            intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}