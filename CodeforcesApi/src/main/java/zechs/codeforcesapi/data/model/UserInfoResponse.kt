package zechs.codeforcesapi.data.model

data class UserInfoResponse(
    val status: String,
    val result: List<InfoResult>?,
    val comment: String?,
)

data class InfoResult(
    val avatar: String,
    val contribution: Int,
    val friendOfCount: Int,
    val handle: String,
    val lastName: String?,
    val lastOnlineTimeSeconds: Int,
    val maxRank: String,
    val maxRating: Int,
    val organization: String?,
    val rank: String,
    val rating: Int,
    val registrationTimeSeconds: Int,
    val titlePhoto: String
)
