package com.example.scorecards.ui

import androidx.recyclerview.widget.DiffUtil
import zechs.codeforcesapi.data.model.UserRating

class RatingDiffCallBack : DiffUtil.ItemCallback<UserRating>() {

    override fun areItemsTheSame(
        oldItem: UserRating,
        newItem: UserRating
    ): Boolean = oldItem.contestId == newItem.contestId

    override fun areContentsTheSame(
        oldItem: UserRating, newItem: UserRating
    ) = oldItem == newItem

}