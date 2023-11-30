package com.example.scorecards.ui

import OnSwipeTouchListener
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.scorecards.R
import com.example.scorecards.databinding.CardDesignBinding

class LeetCodeCard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leet_code_card)

        val leetCodeCardLayout = findViewById<View>(android.R.id.content)

        leetCodeCardLayout.setOnTouchListener(object : OnSwipeTouchListener(this@LeetCodeCard) {
            @SuppressLint("ClickableViewAccessibility")
            override fun onSwipeRight() {
                super.onSwipeRight()
                println("swiped right")
                onBackPressed()
            }
        })
    }
}