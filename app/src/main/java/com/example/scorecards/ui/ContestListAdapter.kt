package com.example.scorecards.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.scorecards.R
import com.example.scorecards.databinding.ItemUpcomingContestBinding
import com.example.scorecards.databinding.RecyclerViewItemBinding
import zechs.codeforcesapi.data.model.Contest

class ContestListAdapter(
    val onClick: (Int) -> Unit
) : ListAdapter<Contest, ContestsViewHolder>(ContestDiffCallBack()) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) =  ContestsViewHolder(
            itemBinding = ItemUpcomingContestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ),
        this)

    override fun onBindViewHolder(holder: ContestsViewHolder, position: Int) {
        val item = getItem(position)
        return holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return  R.layout.item_upcoming_contest
    }
}