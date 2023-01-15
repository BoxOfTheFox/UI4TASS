package data

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

interface ApiService {

    val profiles: Flow<Profiles>

    suspend fun getGraph(graphRequest: GraphRequest): GraphResponse

    suspend fun getProfile(name: String): Profile

    suspend fun getSofifaSimilar(profileName: String, number: Int): SimilarityResponse

    suspend fun getInstaSimilar(profileName: String, number: Int): SimilarityResponse

    suspend fun getComments(profileName: String): CommentsResponse

    suspend fun getTimeRange(startDate: String, endDate: String)

    companion object {
        fun create(): ApiService {
            return ApiServiceImpl(
                client = HttpClient(CIO) {
                    // JSON
                    install(ContentNegotiation) {
                        json(
                            Json {
                                prettyPrint = true
                                isLenient = true
                            }
                        )
                    }
                    // Timeout
                    install(HttpTimeout) {
                        requestTimeoutMillis = 100000L
                        connectTimeoutMillis = 100000L
                        socketTimeoutMillis = 100000L
                    }
                }
            )
        }
    }
}