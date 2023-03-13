package zechs.codeforcesapi.utils.ext

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source

internal fun MockWebServer.response(fileName: String, code: Int): MockResponse? {
    val inputStream = javaClass.classLoader?.getResourceAsStream("api-response/$fileName")

    val source = inputStream?.let { inputStream.source().buffer() }
    return source?.let {
        MockResponse()
            .setResponseCode(code)
            .setBody(source.readString(java.nio.charset.StandardCharsets.UTF_8))
    }
}