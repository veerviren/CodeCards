package zechs.codeforcesapi.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import zechs.codeforcesapi.data.model.UserInfoResponse
import zechs.codeforcesapi.data.model.UserStatusResponse

interface CodeforcesApi {

    @GET("api/user.info")
    suspend fun getUserInfo(
        @Query("handle") handle: String
    ): Response<UserInfoResponse>

    @GET("api/user.status")
    suspend fun getUserStatus(
        @Query("handle") handle: String
    ): Response<UserStatusResponse>

}