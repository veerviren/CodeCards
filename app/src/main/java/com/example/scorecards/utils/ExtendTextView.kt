package com.example.scorecards.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView

fun TextView.setTextColorBasedOnRating(userRating: Int) {
    when {
        userRating <= 1200 -> setTextColor(Color.parseColor("#988f81")) // Newbie
        userRating <= 1400 -> setTextColor(Color.parseColor("#77FF77")) // Pupil
        userRating <= 1600 -> setTextColor(Color.parseColor("#77DDBB")) // Specialist
        userRating <= 1900 -> setTextColor(Color.parseColor("#AAAAFF")) // Expert
        userRating <= 2100 -> setTextColor(Color.parseColor("#ff88ff")) // Candidate Master
        userRating <= 2300 -> setTextColor(Color.parseColor("#FFCC88")) // Master
        userRating <= 2400 -> setTextColor(Color.parseColor("#FFBB55")) // International Master
        userRating <= 2600 -> setTextColor(Color.parseColor("#FF7777")) // Grandmaster
        userRating <= 3000 -> setTextColor(Color.parseColor("#FF3333")) // International Grandmaster
        userRating <= 4000 -> setTextColor(Color.parseColor("#FF1C1F")) // Legendary Grandmaster
        else -> setTextColor(Color.parseColor("#000000")) // black
    }
}

fun TextView.makeFirstLetterUpperCase() {
    val firstChar: String = text.toString().substring(0, 1).uppercase()
    val restChar: String = text.toString().substring(1)
    text = firstChar + restChar
}

fun TextView.canLegendaryGrandmaster(userRating: Int, userName: TextView)
{
    if (userRating >= 3000) {
        changeLegendaryGrandmasterColor(userName)
    }
}
fun changeLegendaryGrandmasterColor(view: TextView) {
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
