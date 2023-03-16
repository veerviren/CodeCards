package com.example.scorecards.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.scorecards.R
import com.example.scorecards.databinding.CardDesignBinding
import com.example.scorecards.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: CardDesignBinding
    private lateinit var handle: String;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CardDesignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        if (intent != null) {
            var text = intent.getStringExtra("text")
            handle = text.toString()
        } else {
            handle = "direction_"
            Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userState.collect { response ->
                    Log.d("MainActivity", response.toString())
                    when (response) {
                        is Resource.Error -> {
                            Toast.makeText(
                                this@MainActivity,
                                response.message!!,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            val user = response.data!!
                            binding.apply {
                                userName.text = user.handle
                                maxRankName.text = getString(R.string.maxRank, user.maxRank)
                                currentRankName.text = getString(R.string.currentRank, user.rank)
                                currentRating.text = getString(R.string.currentRatingNum, user.rating)
                                maxRating.text = getString(R.string.maxRating, user.maxRating)
                                queSolved.text = getString(R.string.noOfProblems, user.totalQuestionsSolved)

                                // logic for color change in handle
                                when {
                                    user.rating <= 1200 -> userName.setTextColor(Color.parseColor("#CCCCCC")) // Newbie
                                    user.rating <= 1400 -> userName.setTextColor(Color.parseColor("#77FF77")) // Pupil
                                    user.rating <= 1600 -> userName.setTextColor(Color.parseColor("#77DDBB")) // Specialist
                                    user.rating <= 1900 -> userName.setTextColor(Color.parseColor("#AAAAFF")) // Expert
                                    user.rating <= 2100 -> userName.setTextColor(Color.parseColor("#ff88ff")) // Candidate Master
                                    user.rating <= 2300 -> userName.setTextColor(Color.parseColor("#FFCC88")) // Master
                                    user.rating <= 2400 -> userName.setTextColor(Color.parseColor("#FFBB55")) // International Master
                                    user.rating <= 2600 -> userName.setTextColor(Color.parseColor("#FF7777")) // Grandmaster
                                    user.rating <= 3000 -> userName.setTextColor(Color.parseColor("#FF3333")) // International Grandmaster
                                    user.rating <= 4000 -> userName.setTextColor(Color.parseColor("#FF1C1F")) // Legendary Grandmaster
                                    else -> userName.setTextColor(Color.parseColor("#000000")) // black
                                }

                                // logic for color change in currentRank

                                when {
                                    user.rating <= 1200 -> currentRankName.setTextColor(Color.parseColor("#CCCCCC")) // Newbie
                                    user.rating <= 1400 -> currentRankName.setTextColor(Color.parseColor("#77FF77")) // Pupil
                                    user.rating <= 1600 -> currentRankName.setTextColor(Color.parseColor("#77DDBB")) // Specialist
                                    user.rating <= 1900 -> currentRankName.setTextColor(Color.parseColor("#AAAAFF")) // Expert
                                    user.rating <= 2100 -> currentRankName.setTextColor(Color.parseColor("#ff88ff")) // Candidate Master
                                    user.rating <= 2300 -> currentRankName.setTextColor(Color.parseColor("#FFCC88")) // Master
                                    user.rating <= 2400 -> currentRankName.setTextColor(Color.parseColor("#FFBB55")) // International Master
                                    user.rating <= 2600 -> currentRankName.setTextColor(Color.parseColor("#FF7777")) // Grandmaster
                                    user.rating <= 3000 -> currentRankName.setTextColor(Color.parseColor("#FF3333")) // International Grandmaster
                                    user.rating <= 4000 -> currentRankName.setTextColor(Color.parseColor("#FF1C1F")) // Legendary Grandmaster
                                    else -> userName.setTextColor(Color.parseColor("#000000")) // black
                                }

                                // logic for color change in MaxRank

                                when {
                                    user.maxRating <= 1200 -> maxRankName.setTextColor(Color.parseColor("#CCCCCC")) // Newbie
                                    user.maxRating <= 1400 -> maxRankName.setTextColor(Color.parseColor("#77FF77")) // Pupil
                                    user.maxRating <= 1600 -> maxRankName.setTextColor(Color.parseColor("#77DDBB")) // Specialist
                                    user.maxRating <= 1900 -> maxRankName.setTextColor(Color.parseColor("#AAAAFF")) // Expert
                                    user.maxRating <= 2100 -> maxRankName.setTextColor(Color.parseColor("#ff88ff")) // Candidate Master
                                    user.maxRating <= 2300 -> maxRankName.setTextColor(Color.parseColor("#FFCC88")) // Master
                                    user.maxRating <= 2400 -> maxRankName.setTextColor(Color.parseColor("#FFBB55")) // International Master
                                    user.maxRating <= 2600 -> maxRankName.setTextColor(Color.parseColor("#FF7777")) // Grandmaster
                                    user.maxRating <= 3000 -> maxRankName.setTextColor(Color.parseColor("#FF3333")) // International Grandmaster
                                    user.maxRating <= 4000 -> maxRankName.setTextColor(Color.parseColor("#FF1C1F")) // Legendary Grandmaster
                                    else -> userName.setTextColor(Color.parseColor("#000000")) // black
                                }

                                // Image logic
                                val imageView: ImageView = findViewById(R.id.userImage)

                                Glide.with(this@MainActivity)
                                    .load(user.titlePhoto)
                                    .placeholder(R.drawable.img)
                                    .error(R.drawable.img_1)
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
                                    .into(imageView)

                            }
                        }
                    }
                }
            }
        }
        viewModel.getUser(handle)
        gradientObserver()
    }

    private fun gradientObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gradient.collect {
                    binding.bottomGradient.background = it
                }
            }
        }
    }


}