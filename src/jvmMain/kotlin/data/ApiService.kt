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
                        requestTimeoutMillis = 15000L
                        connectTimeoutMillis = 15000L
                        socketTimeoutMillis = 15000L
                    }
                }
            )
        }
    }
}