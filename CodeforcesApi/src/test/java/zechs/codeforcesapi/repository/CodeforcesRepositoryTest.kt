package zechs.codeforcesapi.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import zechs.codeforcesapi.data.model.User
import zechs.codeforcesapi.data.remote.CodeforcesApi
import zechs.codeforcesapi.utils.Resource
import zechs.codeforcesapi.utils.ext.response


internal class CodeforcesRepositoryTest {

    private val mockWebServer = MockWebServer()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(CodeforcesApi::class.java)

    private val repository = CodeforcesRepository(api)

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getUser, should User, given 200 response`() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return if (request.path!!.contains("/api/user.info")) {
                    mockWebServer.response("userinfo-200.json", 200)!!
                } else if (request.path!!.contains("/api/user.status")) {
                    mockWebServer.response("userstatus-200.json", 200)!!
                } else {
                    MockResponse().setResponseCode(404)
                }
            }
        }
        runBlocking {
            val actual = repository.getUser("direction_")
            val expected = expectedUser()
            when (actual) {
                is Resource.Error -> {
                    println(actual.message)
                }
                is Resource.Success -> {
                    assertEquals(actual.data, expected)
                    assertEquals(actual.data.totalQuestionsSolved, 16)
                }
            }
        }
    }

    private fun expectedUser(): User {
        return User(
            avatar = "https://userpic.codeforces.org/2731484/avatar/623b9dc16f5fc3c.jpg",
            handle = "direction_",
            titlePhoto = "https://userpic.codeforces.org/2731484/title/19410c8d73316f13.jpg",
//            fullName = "Viren Variya",
            rank = "newbie",
            rating = 1168,
            maxRating = 1169,
            maxRank = "newbie",
            totalQuestionsSolved = 16
        )
    }

}