package com.example.scorecards.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.toUpperCase
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GestureDetectorCompat
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
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Math.abs
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: CardDesignBinding
    private lateinit var handle: String;
    private var mShowShimmer = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CardDesignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // start the shimmer effect
        val shimmerFrameLayout = findViewById<ShimmerFrameLayout>(R.id.shimmer_view_container)
        shimmerFrameLayout.showShimmer(true)

        // bottom sheet dialog
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(applicationContext)
            .inflate(
                R.layout.activity_detail_card,
                findViewById(R.id.detailCardLayout)
            )
        val button = findViewById<Button>(R.id.show)
        button.setOnClickListener {
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }

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
                            shimmerFrameLayout.hideShimmer()
                        }
                        is Resource.Loading -> {
                            shimmerFrameLayout.showShimmer(true)
                            binding.apply {
                                currentRating.text = getString(R.string.currentRatingNum, 0)
                                maxRating.text = getString(R.string.maxRating, 0)
                                queSolved.text = getString(R.string.noOfProblems, 0)
                            }
                        }
                        is Resource.Success -> {
                            // stop the shimmer effect
                            shimmerFrameLayout.hideShimmer()

                            val user = response.data!!
                            binding.apply {
                                userName.text = user.handle
                                maxRankName.text = getString(R.string.maxRank, user.maxRank)
                                currentRankName.text = getString(R.string.currentRank, user.rank)
                                currentRating.text =
                                    getString(R.string.currentRatingNum, user.rating)
                                maxRating.text = getString(R.string.maxRating, user.maxRating)
                                queSolved.text =
                                    getString(R.string.noOfProblems, user.totalQuestionsSolved)


                                // break 2 words Name
                                maxRankName.text =
                                    getString(R.string.maxRank, user.maxRank).replace(" ", "\n")
                                currentRankName.text =
                                    getString(R.string.currentRank, user.rank).replace(" ", "\n")
                                maxRankName.text.toString().trim()
                                currentRankName.text.toString().trim()
                                print(maxRankName)
                                print(currentRankName)

                                // Capitalising first letter of Ranking name
                                makeFirstLetterUpperCase(currentRankName)
                                makeFirstLetterUpperCase(maxRankName)

                                // logic for color change in handle
                                setTextViewColor(userName, user.rating)

                                // logic for color change in currentRank
                                setTextViewColor(currentRankName, user.rating)

                                // logic for color change in MaxRank
                                setTextViewColor(maxRankName, user.maxRating)

                                // color formatting for legendary grandmaster
                                if (user.rating >= 3000) {

                                    // fro user handle
                                    changeLegendaryGrandmasterColor(userName)
                                }

                                // Image logic
                                val imageView: ImageView = findViewById(R.id.userImage)

                                Glide.with(this@MainActivity)
                                    .load(user.titlePhoto)
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
            userRating <= 1200 -> view.setTextColor(Color.parseColor("#988f81")) // Newbie
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

    private fun makeFirstLetterUpperCase(view: TextView) {
        var fistChar: String = view.text.toString().substring(0, 1).uppercase()
        var restChar: String = view.text.toString().substring(1)
        view.text = fistChar + restChar
    }
    private fun changeLegendaryGrandmasterColor(view: TextView) {
        val spannableString = SpannableString(view.text)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#000000"))
        spannableString.setSpan(
            colorSpan,
            0,
            1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.text = spannableString
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