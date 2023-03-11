package com.example.scorecards.ui.card

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SwipCard(
    handle: String,
    rank: String,
    contestRating: Int,
    maxRank: String,
    maxRating: Int,
    totalQuestionsSolved: Int,
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight()
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            elevation = 8.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Add the image here

                Text(
                    text = handle,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Rank: $rank",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.DarkGray
                )

                Text(
                    text = "Contest Rating: $contestRating",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.DarkGray
                )

                Text(
                    text = "Max Rank: $maxRank",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.DarkGray
                )

                Text(
                    text = "Max Rating: $maxRating",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.DarkGray
                )

                Text(
                    text = "Total Questions Solved: $totalQuestionsSolved",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSwipCard() {
    SwipCard(
        handle = "direction_",
        rank = "Newbie",
        contestRating = 234,
        maxRank = "International grandMaster",
        maxRating = 4000,
        totalQuestionsSolved = 163,
    )
}
