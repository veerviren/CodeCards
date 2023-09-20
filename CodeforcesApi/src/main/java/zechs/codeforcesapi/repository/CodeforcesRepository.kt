package zechs.codeforcesapi.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import zechs.codeforcesapi.data.model.Contest
import zechs.codeforcesapi.data.model.User
import zechs.codeforcesapi.data.model.UserRating
import zechs.codeforcesapi.data.remote.CodeforcesApi
import zechs.codeforcesapi.utils.Resource
import zechs.codeforcesapi.utils.runInTryCatch

class CodeforcesRepository(
    private val api: CodeforcesApi
) {
    suspend fun getUser(handle: String): Resource<User> {
        return withContext(Dispatchers.IO) {
            return@withContext runInTryCatch(
                tryBlock = {
                    val userInfo = async { api.getUserInfo(handle) }
                    val userStatus = async { api.getUserStatus(handle) }
                    val user = userInfo.await()
                    val status = userStatus.await()
                    if (user.isSuccessful && status.isSuccessful) {
                        val statsResult = status.body()!!.result
                        val result = user.body()!!.result
                        if (result == null || statsResult == null) {
                            val error = user.body()!!.comment
                                ?: status.body()!!.comment
                                ?: "Something went wrong"
                            return@runInTryCatch Resource.Error(error)
                        } else {
                            val count = status.body()!!.getAcceptedProblems()
                            println("submission : ${status.body()!!.getSubmittedProblemsByday()}")
                            val info = result[0]
                            return@runInTryCatch Resource.Success(
                                User(
                                    avatar = info.avatar,
                                    titlePhoto = info.titlePhoto,
                                    handle = info.handle,
                                    rank = info.rank,
                                    rating = info.rating,
                                    maxRating = info.maxRating,
                                    maxRank = info.maxRank,
                                    totalQuestionsSolved = count ?: 0
                                )
                            )
                        }
                    }
                    return@runInTryCatch Resource.Error("Network error")
                },
                catchBlock = { err ->
                    err.printStackTrace()
                    val msg = err.localizedMessage ?: "Something went wrong"
                    return@runInTryCatch Resource.Error(msg)
                }
            )
        }
    }

    suspend fun getContests(): Resource<List<Contest>> {
        return withContext(Dispatchers.IO) {
            return@withContext runInTryCatch(
                tryBlock = {
                    val contests = api.getContestList()
                    if (contests.isSuccessful) {
                        val contestResult = contests.body()!!.result
                        if (contestResult == null) {
                            val error = "Something went wrong"
                            return@runInTryCatch Resource.Error(error)
                        } else {

                            return@runInTryCatch Resource.Success(
                                contestResult
                            )
                        }
                    }
                    return@runInTryCatch Resource.Error("Network error")
                },
                catchBlock = { err ->
                    err.printStackTrace()
                    val msg = err.localizedMessage ?: "Something went wrong"
                    return@runInTryCatch Resource.Error(msg)
                }
            )
        }
    }

    suspend fun getUserRating(handle: String): Resource<List<UserRating>> {
        return withContext(Dispatchers.IO) {
            return@withContext runInTryCatch(
                tryBlock = {
                    val userRatingInfo = async { api.getUserRating(handle) }
                    val userRating = userRatingInfo.await()
                    if (userRating.isSuccessful) {
                        val result = userRating.body()?.result
                        if (result == null) {
                            val error = userRating.body()?.comment ?: "Something went wrong"
                            return@runInTryCatch Resource.Error(error)
                        } else {
                            val userContestRating = mutableListOf<UserRating>()

                            for (i in result) {
                                val userContestInfo = UserRating(
                                    oldRating = i.oldRating,
                                    newRating = i.newRating,
                                    contestName = i.contestName,
                                    contestId = i.contestId
                                )
                                userContestRating.add(userContestInfo)
                            }

                            return@runInTryCatch Resource.Success(userContestRating)
                        }
                    }
                    return@runInTryCatch Resource.Error("Network error")
                },
                catchBlock = { err ->
                    err.printStackTrace()
                    val msg = err.localizedMessage ?: "Something went wrong"
                    return@runInTryCatch Resource.Error(msg)
                }
            )
        }
    }
}
