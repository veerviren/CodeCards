package com.example.scorecards.ui

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.scorecards.R
import com.example.scorecards.databinding.ItemUpcomingContestBinding
import zechs.codeforcesapi.data.model.UserRating

class RatingViewHolder (
    private val itemBinding: ItemUpcomingContestBinding,
) : RecyclerView.ViewHolder(itemBinding.root){

    fun bind(userRating: UserRating) {
        itemBinding.apply {
            contestName.text = userRating.contestName
            val delta = userRating.newRating - userRating.oldRating
            if(delta > 0)
            {
                contestTime.text = "+$delta"
                itemBinding.root.setBackgroundColor(itemView.context.resources.getColor(R.color.light_green))
            }
            else{
                contestTime.text = delta.toString()
                itemBinding.root.setBackgroundColor(itemView.context.resources.getColor(com.google.android.material.R.color.design_dark_default_color_error))
            }
            contestTime.textSize = 20F
        }
    }


}