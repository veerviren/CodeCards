package zechs.codeforcesapi.data.model

data class UserStatusResponse(
    val status: String,
    val result: List<StatusResult>?,
    val comment: String?,
) {

    fun getAcceptedProblems(): Int? {
        return result
            ?.filter { it.verdict != null && it.verdict=="OK"}
            ?.size
    }

}

data class StatusResult(
//    val author: Author,
//    val contestId: Int,
//    val creationTimeSeconds: Int,
//    val id: Int,
//    val memoryConsumedBytes: Int,
//    val passedTestCount: Int,
//    val problem: Problem,
//    val programmingLanguage: String,
//    val relativeTimeSeconds: Int,
//    val testset: String,
//    val timeConsumedMillis: Int,
    val verdict: String?
)

data class Problem(
    val contestId: Int,
    val index: String,
    val name: String,
    val points: Double,
    val rating: Int?,
    val tags: List<String>,
    val type: String
)


data class Member(
    val handle: String
)

data class Author(
    val contestId: Int,
    val ghost: Boolean,
    val members: List<Member>,
    val participantType: String,
    val room: Int,
    val startTimeSeconds: Int
)