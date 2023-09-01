package com.example.scorecards.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scorecards.R
import zechs.codeforcesapi.data.model.Contest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class UpcomingContestAdapter(private val contests: List<Contest>) :
    RecyclerView.Adapter<UpcomingContestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contestName: TextView = view.findViewById(R.id.contestName)
        val contestTime: TextView = view.findViewById(R.id.contestTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_contest, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contest = contests[position]
        holder.contestName.text = contest.name
        val startTimeMillis = contest.startTimeSeconds * 1000L
        val formattedTime = formatToDesiredDateFormat(startTimeMillis)
        holder.contestTime.text = formattedTime
    }

    override fun getItemCount(): Int = contests.size

    private fun formatToDesiredDateFormat(timeMillis: Long): String {
        val sdf = SimpleDateFormat("MMM/dd/yyyy HH:mm'UTC'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timeMillis))
    }
}
