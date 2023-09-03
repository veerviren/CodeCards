package zechs.codeforcesapi.data.model

class UserRatingResponse(
    val status: String,
    val result: List<ContestInfo>?,
    val comment: String?,
)

data class ContestInfo(
    val contestId: Int,
    val contestName: String,
    val handle: String,
    val rank: Int,
    val ratingUpdateTimeSeconds: Long,
    val oldRating: Int,
    val newRating: Int
)
