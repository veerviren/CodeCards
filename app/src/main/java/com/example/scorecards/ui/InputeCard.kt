package com.example.scorecards.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.scorecards.R
import com.example.scorecards.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch


@AndroidEntryPoint
class InputeCard : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inpute_card)

        val imageView: ImageView = findViewById(R.id.background_img)
        val radius = 50

        Glide.with(this)
            .load(R.drawable.golden_background)
            .transform(BlurTransformation(radius))
            .into(imageView)

        val button: Button = findViewById(R.id.submit_button)

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUid = currentUser?.uid
        val userInfoReference: DatabaseReference? = userUid?.let {
            database.getReference("users").child(it).child("user_info")
        }

        //Check handle is available in database
        userInfoReference?.get()?.addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.hasChild("handle")) {
                val handle = dataSnapshot.child("handle").value.toString()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("text", handle)
                startActivity(intent)
            }
        }

        button.setOnClickListener {
            val editText = findViewById<EditText>(R.id.user_input)
            val handle = editText.text.toString()

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.userState.collect { response ->
                        Log.d("MainActivity", response.toString())
                        when (response) {
                            is Resource.Error -> {
                                Toast.makeText(this@InputeCard, "Error", Toast.LENGTH_SHORT).show()
                            }

                            is Resource.Loading -> {
                                Toast.makeText(this@InputeCard, "Loading", Toast.LENGTH_SHORT).show()
                            }

                            is Resource.Success -> {
                                val user = response.data
                                if (user != null) {
                                    val intent = Intent(this@InputeCard, MainActivity::class.java)
                                    intent.putExtra("text", handle);
                                    startActivity(intent);
                                }

                                //save handle in database
                                userInfoReference?.child("handle")?.setValue(handle)
                            }
                        }
                    }
                }
            }

            viewModel.getUser(handle)
        }

    }
}