package com.example.scorecards.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.scorecards.R
import com.example.scorecards.databinding.CardDesignBinding
import com.example.scorecards.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: CardDesignBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CardDesignBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                                maxRank.text = getString(R.string.maxRank, user.maxRank)
                                currentRank.text = getString(R.string.currentRank, user.rank)
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
                            }
                        }
                    }
                }
            }
        }
        viewModel.getUser("Geothermal")
    }
}