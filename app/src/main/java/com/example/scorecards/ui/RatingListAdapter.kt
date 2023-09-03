package com.example.scorecards.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.scorecards.R
import com.example.scorecards.databinding.ItemUpcomingContestBinding
import zechs.codeforcesapi.data.model.UserRating

class RatingListAdapter : ListAdapter<UserRating, RatingViewHolder>(RatingDiffCallBack()) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) =  RatingViewHolder(
        itemBinding = ItemUpcomingContestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        ),
        )

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val item = getItem(position)
        return holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return  R.layout.item_upcoming_contest
    }
}