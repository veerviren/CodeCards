package com.example.scorecards.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.scorecards.R

class DetailCard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_card)

        val btn: Button = findViewById(R.id.add_friend_handle)

        btn.setOnClickListener {
            Log.d("DetailCard", "Button clicked")
            Toast.makeText(this, "btn clicked", Toast.LENGTH_SHORT).show()
            Log.d("DetailCard", "Toast shown")
        }
    }
}