package com.example.scorecards.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.scorecards.R
import com.example.scorecards.databinding.ActivityProfileBinding
import com.example.scorecards.utils.canLegendaryGrandmaster
import com.example.scorecards.utils.setTextColorBasedOnRating
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val editUsernameButton = findViewById<Button>(R.id.editUsernameButton)
        val logOutButton = findViewById<Button>(R.id.logoutButton)
        val userName = findViewById<TextView>(R.id.profileUserName)
        val sharedPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE)

        val shimmerFrameLayout = findViewById<ShimmerFrameLayout>(R.id.profileCardshimmerLayout)
        shimmerFrameLayout.hideShimmer()
        val profileImageUrl = intent.getStringExtra("profileImage")

        Glide.with(this)
            .load(profileImageUrl)
            .placeholder(R.drawable.loading_effect)
            .error(R.drawable.error)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ) = false

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.let {
                        viewModel.setImageDrawable(resource)
                    }
                    return false
                }
            })
            .into(profileImage)

        val rating = intent.getIntExtra("rating", 0)

        userName.text = intent.getStringExtra("handle")

        userName.setTextColorBasedOnRating(rating)
        userName.canLegendaryGrandmaster(rating)

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

            intent = Intent(this, Login::class.java)
            startActivity(intent)
        }


        gradientObserver()
    }

    private fun removeHandleFromDatabase(shimmerFrameLayout: ShimmerFrameLayout) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUid = currentUser?.uid
        val userInfoReference: DatabaseReference? = userUid?.let {
            database.getReference("users").child(it).child("user_info")
        }
        shimmerFrameLayout.showShimmer(true)

        userInfoReference?.child("handle")?.removeValue()?.addOnCompleteListener(this) {
            shimmerFrameLayout.hideShimmer()
        }
    }

    private fun gradientObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gradient.collect {
                    binding.profileBottomGradient.background = it
                }
            }
        }
    }
}