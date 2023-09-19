package com.example.scorecards.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.scorecards.R
import com.example.scorecards.utils.canLegendaryGrandmaster
import com.example.scorecards.utils.setTextColorBasedOnRating
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

        val shimmerFrameLayout = findViewById<ShimmerFrameLayout>(R.id.profileCardshimmerLayout)
        shimmerFrameLayout.hideShimmer()
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
            removeHandleFromDatabase(shimmerFrameLayout)

            intent = Intent(this, InputeCard::class.java)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {

            val editor = sharedPreferences.edit()
            editor.putString("EMAIL", "")
            editor.putString("PASSWORD", "")
            editor.apply()

            removeHandleFromDatabase(shimmerFrameLayout)

            intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun removeHandleFromDatabase(shimmerFrameLayout: ShimmerFrameLayout) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUid = currentUser?.uid
        val userInfoReference: DatabaseReference? = userUid?.let {
            database.getReference("users").child(it).child("user_info")
        }

        shimmerFrameLayout.showShimmer(true)

        userInfoReference?.get()?.addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.hasChild("handle")) {
                userInfoReference.child("handle").removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            shimmerFrameLayout.hideShimmer()
                        } else {
                            val error = task.exception
                            Log.e("FirebaseError", "Failed to remove handle: $error")
                            Toast.makeText(
                                applicationContext,
                                "Failed to remove handle. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                            shimmerFrameLayout.hideShimmer()
                        }
                    }
            } else {
                shimmerFrameLayout.hideShimmer()
            }
        }
    }


}