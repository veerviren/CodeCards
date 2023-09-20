package zechs.codeforcesapi.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserStatusResponse(
    val status: String,
    val result: List<StatusResult>?,
    val comment: String?,
) {
    fun getSubmittedProblemsByday(): MutableMap<String, Int> {
        val problemCountByDay = mutableMapOf<String, Int>()

        // Define a date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        for (contest in result!!) {
            val date = Date(contest.creationTimeSeconds * 1000)
            val day = dateFormat.format(date)

            problemCountByDay[day] = problemCountByDay.getOrDefault(day, 0) + 1
        }

        return problemCountByDay
    }
    fun getAcceptedProblems(): Int? {
        return result
            ?.filter { it.verdict != null && it.verdict=="OK"}
            ?.size
    }

}

data class StatusResult(
    val verdict: String?,
    val creationTimeSeconds: Long
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