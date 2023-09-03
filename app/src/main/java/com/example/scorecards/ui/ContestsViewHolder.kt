package com.example.scorecards.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.scorecards.databinding.ItemUpcomingContestBinding
import zechs.codeforcesapi.data.model.Contest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ContestsViewHolder(
    private val itemBinding: ItemUpcomingContestBinding,
    private val adapter: ContestListAdapter
) : RecyclerView.ViewHolder(itemBinding.root){

    fun bind(contest: Contest) {
        itemBinding.apply {
            contestName.text = contest.name
            val startTimeMillis = contest.startTimeSeconds * 1000L
            val formattedTime = formatToDesiredDateFormat(startTimeMillis)
            contestTime.text = formattedTime

            root.setOnClickListener{
                adapter.onClick.invoke(contest.id)
            }
        }
    }

    private fun formatToDesiredDateFormat(timeMillis: Long): String {
        val sdf = SimpleDateFormat("MMM/dd/yyyy HH:mm'UTC'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timeMillis))
    }
}