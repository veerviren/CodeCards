package com.example.scorecards.ui
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.scorecards.R


class DetailCard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_card)

        val addButton = findViewById<Button>(R.id.add_friend_handle)


        addButton.setOnClickListener {
            print("btn clicked")
            Toast.makeText(
                this,
                "Button Clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}