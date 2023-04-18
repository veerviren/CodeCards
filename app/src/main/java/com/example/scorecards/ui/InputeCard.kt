package com.example.scorecards.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.scorecards.R
import jp.wasabeef.glide.transformations.BlurTransformation


class InputeCard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inpute_card)

        val imageView: ImageView = findViewById(R.id.background_img)
        val radius = 50

        Glide.with(this)
            .load(R.drawable.golden_background)
            .transform(BlurTransformation(radius))
            .into(imageView)

        val button: Button = findViewById(R.id.submit_button)
        button.setOnClickListener {
            val editText = findViewById<EditText>(R.id.user_input)
            val text = editText.text.toString()

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("text", text);
            startActivity(intent);
        }

    }
}