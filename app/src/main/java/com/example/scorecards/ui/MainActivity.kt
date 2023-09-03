package com.example.scorecards.ui

import HorizontalIndicator
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
import zechs.codeforcesapi.data.model.UserRating


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: CardDesignBinding
    private lateinit var handle: String;

    //recycleview
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CardDesignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // start the shimmer effect
        val shimmerFrameLayout = findViewById<ShimmerFrameLayout>(R.id.shimmer_view_container)
        shimmerFrameLayout.showShimmer(true)

        // bottom sheet dialog
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

        //recycleview
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

        // logic to add friend handle
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
                            Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()

                            val upcomingContests = response.data?.let { getUpcomingContests(it) }
//                            Log.d("MainActivity", "Upcoming contests: $upcomingContests")

                            if (!upcomingContests.isNullOrEmpty()) {
                                // Assuming you have references to your RecyclerView, "next" button, and "previous" button
                                val nextButton = bottomSheetView.findViewById<Button>(R.id.nextButton)
                                val prevButton = bottomSheetView.findViewById<Button>(R.id.prevButton)

                                contestAdapter.submitList(upcomingContests.toList())

                                var currentPage = 0
                                updateButtonVisibility(currentPage, recyclerView, prevButton, nextButton)

                                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                        super.onScrolled(recyclerView, dx, dy)
                                        currentPage = getCurrentPage(recyclerView)
                                        updateButtonVisibility(currentPage, recyclerView, prevButton, nextButton)
                                    }
                                })

                                nextButton.setOnClickListener {
                                    if (currentPage < upcomingContests.size - 1) {
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
                            } else {
                                recyclerView.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

//        lifecycleScope.launch {
//            Log.d("MainActivity","rated user launched")
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.ratedUserState.collect{response ->
//                    when (response) {
//                        is Resource.Error -> {
//                            Log.d("MainActivity","rated user error")
//                            Toast.makeText(
//                                this@MainActivity,
//                                response.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//
//                        is Resource.Loading -> {
//                            Log.d("MainActivity","rated user loading")
//                            Toast.makeText(
//                                this@MainActivity,
//                                "Loading",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//
//                        is Resource.Success -> {
//                            Log.d("MainActivity","rated user Success")
//                            Toast.makeText(this@MainActivity, "Success2", Toast.LENGTH_SHORT).show()
//
//                            val ratedUsers = response.data?.sortedByDescending { it.rating }
//
//                            Log.d("MainActivity","rated user list: ${response.data}")
//                        }
//                    }
//                }
//            }
//        }

        viewModel.getUser(handle.trim())

        viewModel.getContest()

//        viewModel.getUserRating()

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

    fun getUpcomingContests(upcomingContests: List<Contest>): List<Contest> {
        // Filter contests that are in the "BEFORE" phase
        val beforeContests = upcomingContests.filter { it.phase == "BEFORE" }

        // Sort the contests by start time in ascending order
        val sortedContests = beforeContests.sortedBy { it.startTimeSeconds }

        return sortedContests
    }

    fun getRatedUsers(
        data: List<UserRating>,
        enteredHandle: String
    ): List<UserRating> {

        val sortedUsers = data.sortedBy { it.rating }

        val givenUserIndex = sortedUsers.indexOf(data.find { it.handle == enteredHandle })

        val higherRatedUsers = sortedUsers
            .subList(maxOf(0, givenUserIndex + 1), minOf(sortedUsers.size, givenUserIndex + 6))

        val lowerRatedUsers = sortedUsers
            .subList(maxOf(0, givenUserIndex - 5), minOf(sortedUsers.size, givenUserIndex))

        val combinedUsers = mutableListOf<UserRating>()
        combinedUsers.addAll(higherRatedUsers)
        combinedUsers.addAll(lowerRatedUsers)

        return combinedUsers
    }

}