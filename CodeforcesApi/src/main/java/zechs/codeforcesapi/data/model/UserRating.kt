package zechs.codeforcesapi.data.model

data class UserRating(
    val oldRating: Int,
    val newRating: Int,
    val contestName: String,
    val contestId: Int
    )
