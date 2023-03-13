package zechs.codeforcesapi.data.model

data class User(
    val avatar: String,
    val handle: String,
    val fullName: String,
    val rank: String,
    val rating: Int,
    val maxRank: String,
    val maxRating: Int,
    val totalQuestionsSolved: Int
)

