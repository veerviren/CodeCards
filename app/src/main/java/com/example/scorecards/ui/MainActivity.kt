package com.example.scorecards.ui

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
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

        //loding animation
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.loading_animation)


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
                            progressDialog.dismiss()
                        }
                        is Resource.Loading -> {
                            progressDialog.show()
                        }
                        is Resource.Success -> {
                            progressDialog.dismiss()
                            val user = response.data!!
                            binding.apply {
                                userName.text = user.handle
                                maxRankName.text = getString(R.string.maxRank, user.maxRank)
                                currentRankName.text = getString(R.string.currentRank, user.rank)
                                currentRating.text = getString(R.string.currentRatingNum, user.rating)
                                maxRating.text = getString(R.string.maxRating, user.maxRating)
                                queSolved.text = getString(R.string.noOfProblems, user.totalQuestionsSolved)

                                // break 2 words Name
                                maxRankName.text = getString(R.string.maxRank, user.maxRank).replace(" ", "\n")
                                currentRankName.text = getString(R.string.currentRank, user.rank).replace(" ", "\n")
                                maxRankName.text.toString().trim()
                                currentRankName.text.toString().trim()
                                print(maxRankName)
                                print(currentRankName)

                                // logic for color change in handle
                                setTextViewColor(userName, user.rating)

                                // logic for color change in currentRank
                                setTextViewColor(currentRankName, user.rating)

                                // logic for color change in MaxRank
                                setTextViewColor(maxRankName, user.maxRating)

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

    private fun setTextViewColor(view: TextView, userRating: Int) {
        when {
            userRating <= 1200 -> view.setTextColor(Color.parseColor("#CCCCCC")) // Newbie
            userRating <= 1400 -> view.setTextColor(Color.parseColor("#77FF77")) // Pupil
            userRating <= 1600 -> view.setTextColor(Color.parseColor("#77DDBB")) // Specialist
            userRating <= 1900 -> view.setTextColor(Color.parseColor("#AAAAFF")) // Expert
            userRating <= 2100 -> view.setTextColor(Color.parseColor("#ff88ff")) // Candidate Master
            userRating <= 2300 -> view.setTextColor(Color.parseColor("#FFCC88")) // Master
            userRating <= 2400 -> view.setTextColor(Color.parseColor("#FFBB55")) // International Master
            userRating <= 2600 -> view.setTextColor(Color.parseColor("#FF7777")) // Grandmaster
            userRating <= 3000 -> view.setTextColor(Color.parseColor("#FF3333")) // International Grandmaster
            userRating <= 4000 -> view.setTextColor(Color.parseColor("#FF1C1F")) // Legendary Grandmaster
            else -> view.setTextColor(Color.parseColor("#000000")) // black
        }
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