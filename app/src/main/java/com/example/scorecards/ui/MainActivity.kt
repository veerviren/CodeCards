package com.example.scorecards.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.scorecards.R
import com.example.scorecards.databinding.CardDesignBinding
import com.example.scorecards.utils.Constants.Companion.CODEFORCES_API_URL
import com.example.scorecards.utils.Resource
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zechs.codeforcesapi.data.model.Contest


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: CardDesignBinding
    private lateinit var handle: String;

    private lateinit var recyclerView: RecyclerView

    private val friendsAdapter by lazy {
        FriendListAdapter(onDelete = { viewModel.removeFriend(it)})
    }

    private val contestAdapter by lazy {
        ContestListAdapter(onClick = {id->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${CODEFORCES_API_URL}/contestRegistration/$id"))
            startActivity(intent)
        })
    }
    private val ratingListAdapter by lazy {
        RatingListAdapter()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CardDesignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val shimmerFrameLayout = findViewById<ShimmerFrameLayout>(R.id.shimmer_view_container)
        shimmerFrameLayout.showShimmer(true)

        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(this)
            .inflate(
                R.layout.activity_detail_card,
                findViewById(R.id.detailCardLayout)
            )

        val button = findViewById<Button>(R.id.show)
        button.setOnClickListener {
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }

        recyclerView = bottomSheetView.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        recyclerView.adapter = friendsAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.friends.collect {
                    println("HEREEEEEE  =  ${it}")
                    friendsAdapter.submitList(it)
                }
            }
        }

        val addButton = bottomSheetView.findViewById<Button>(R.id.add_friend_handle)

        addButton.setOnClickListener {
            println("btn clicked")
            Toast.makeText(
                this,
                "Button Clicked",
                Toast.LENGTH_SHORT
            ).show()
            val editText = EditText(this)
            editText.setTextColor(Color.BLACK)
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Add friend handle")
            builder.setView(editText)
            builder.setPositiveButton("OK") { _, _ ->
                val handle = editText.text.toString()
                handle.trim()
                Toast.makeText(
                    this,
                    handle + " added",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.addFriend(handle)
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
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

                                maxRankName.text =
                                    getString(R.string.maxRank, user.maxRank).replace(" ", "\n")
                                currentRankName.text =
                                    getString(R.string.currentRank, user.rank).replace(" ", "\n")
                                maxRankName.text.toString().trim()
                                currentRankName.text.toString().trim()
                                print(maxRankName)
                                print(currentRankName)

                                makeFirstLetterUpperCase(currentRankName)
                                makeFirstLetterUpperCase(maxRankName)

                                setTextViewColor(userName, user.rating)

                                setTextViewColor(currentRankName, user.rating)

                                setTextViewColor(maxRankName, user.maxRating)

                                if (user.rating >= 3000) {
                                    changeLegendaryGrandmasterColor(userName)
                                }

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

        recyclerView = bottomSheetView.findViewById(R.id.upcoming_contest_recyclerview)
        recyclerView.adapter = contestAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contestState.collect{response ->
                    when (response) {
                        is Resource.Error -> {
                            Toast.makeText(
                                this@MainActivity,
                                response.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Loading -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Loading",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Success -> {

                            val upcomingContests = response.data?.let { getUpcomingContests(it) }

                            if (!upcomingContests.isNullOrEmpty()) {
                                val nextButtonContest = bottomSheetView.findViewById<Button>(R.id.nextButton)
                                val prevButtonContest = bottomSheetView.findViewById<Button>(R.id.prevButton)

                                contestAdapter.submitList(upcomingContests.toList())

                                var currentPage = 0
                                updateButtonVisibility(currentPage, recyclerView, prevButtonContest, nextButtonContest)

                                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                        super.onScrolled(recyclerView, dx, dy)
                                        currentPage = getCurrentPage(recyclerView)
                                        updateButtonVisibility(currentPage, recyclerView, prevButtonContest, nextButtonContest)
                                    }
                                })

                                setupNavigationButtons(recyclerView, nextButtonContest, prevButtonContest)
                            } else {
                                recyclerView.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        val userProgressRecyclerView = bottomSheetView.findViewById<RecyclerView>(R.id.UserProgressRecyclerview)
        userProgressRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        userProgressRecyclerView.adapter = ratingListAdapter

        snapHelper.attachToRecyclerView(userProgressRecyclerView)

        lifecycleScope.launch {
            Log.d("MainActivity","rated user launched")
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ratedUserState.collect{response ->
                    when (response) {
                        is Resource.Error -> {
                            Log.d("MainActivity","rated user error")
                            Toast.makeText(
                                this@MainActivity,
                                response.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Loading -> {
                            Log.d("MainActivity","rated user loading")
                            Toast.makeText(
                                this@MainActivity,
                                "Loading",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Success -> {
                            Log.d("MainActivity","rated user Success")
                            Toast.makeText(this@MainActivity, "Success2", Toast.LENGTH_SHORT).show()

                            val contestRating = response.data

                            Log.d("MainActivity","rated user list: $contestRating")

                            if(!contestRating.isNullOrEmpty())
                            {
                                val nextButtonUserProgress = bottomSheetView.findViewById<Button>(R.id.UserProgressNextButton)
                                val prevButtonUserProgress = bottomSheetView.findViewById<Button>(R.id.UserProgressPrevButton)

                                ratingListAdapter.submitList(contestRating.toList().sortedByDescending { it.contestId })

                                var currentPage = 0
                                updateButtonVisibility(currentPage, userProgressRecyclerView, prevButtonUserProgress, nextButtonUserProgress)

                                userProgressRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                        super.onScrolled(recyclerView, dx, dy)
                                        currentPage = getCurrentPage(recyclerView)
                                        updateButtonVisibility(currentPage, recyclerView, prevButtonUserProgress, nextButtonUserProgress)
                                    }
                                })

                                setupNavigationButtons(userProgressRecyclerView, nextButtonUserProgress, prevButtonUserProgress)

                            }
                            else {
                                recyclerView.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        viewModel.getUser(handle.trim())

        viewModel.getContest()

        viewModel.getUserRating(handle.trim())

        gradientObserver()
    }

    private fun getCurrentPage(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
            return firstVisibleItemPosition
        }
        return 0
    }

    private fun updateButtonVisibility(currentPage: Int, recyclerView: RecyclerView, prevButton: Button, nextButton: Button) {
        val isAtFirstPage = currentPage == 0
        val isAtLastPage = currentPage == recyclerView.adapter?.itemCount?.minus(1)

        prevButton.visibility = if (isAtFirstPage) View.GONE else View.VISIBLE
        nextButton.visibility = if (isAtLastPage) View.GONE else View.VISIBLE
    }

    private fun setupNavigationButtons(recyclerView: RecyclerView, nextButton: Button, prevButton: Button) {
        var currentPage = 0

        nextButton.setOnClickListener {
            if (currentPage < recyclerView.adapter?.itemCount?.minus(1) ?: 0) {
                currentPage++
                recyclerView.smoothScrollToPosition(currentPage)
            }
        }

        prevButton.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                recyclerView.smoothScrollToPosition(currentPage)
            }
        }
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

    private fun getUpcomingContests(upcomingContests: List<Contest>): List<Contest> {
        val beforeContests = upcomingContests.filter { it.phase == "BEFORE" }

        return beforeContests.sortedBy { it.startTimeSeconds }
    }
}