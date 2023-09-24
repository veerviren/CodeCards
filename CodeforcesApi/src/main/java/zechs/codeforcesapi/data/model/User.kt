package zechs.codeforcesapi.data.model

import java.util.concurrent.SubmissionPublisher

data class User(
    val avatar: String,
    val handle: String,
    val titlePhoto: String,
    val rank: String,
    val rating: Int,
    val maxRank: String,
    val maxRating: Int,
    val totalQuestionsSolved: Int,
    val toatlQuestionsSubmittedByDate: MutableMap<String, Int>
)

