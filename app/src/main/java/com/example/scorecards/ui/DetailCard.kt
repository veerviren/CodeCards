package com.example.scorecards.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.scorecards.R
import com.example.scorecards.utils.Resource
import kotlinx.coroutines.launch


class DetailCard : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_card)

        Toast.makeText(
            this@DetailCard,
            "Detail page started",
            Toast.LENGTH_SHORT
        ).show()


        viewModel.getContest()
    }
}
