package com.example.scorecards.ui

import androidx.recyclerview.widget.DiffUtil
import zechs.codeforcesapi.data.model.Contest

class ContestDiffCallBack : DiffUtil.ItemCallback<Contest>() {

    override fun areItemsTheSame(
        oldItem: Contest,
        newItem: Contest
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Contest, newItem: Contest
    ) = oldItem == newItem

}