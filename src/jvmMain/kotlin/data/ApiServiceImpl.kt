package data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {

    override val profiles: Flow<Profiles> = flow { emit(client.get(ApiRoutes.PROFILES).body()) }

    override suspend fun getGraph(graphRequest: GraphRequest): GraphResponse = withContext(Dispatchers.IO) {
        client.get(ApiRoutes.GRAPH) {
            contentType(ContentType.Application.Json)
            setBody(graphRequest)
        }.body()
    }

    override suspend fun getProfile(name: String): Profile =
        client.get("${ApiRoutes.PROFILE}/${name.replace(" ", "%20")}").body()

    override suspend fun getSofifaSimilar(profileName: String, number: Int): SimilarityResponse =
        client.get("${ApiRoutes.Similar.SOFIFA}/$profileName/$number").body()

    override suspend fun getInstaSimilar(profileName: String, number: Int): SimilarityResponse =
        client.get("${ApiRoutes.Similar.INSTA}/$profileName/$number").body()

    override suspend fun getComments(profileName: String): CommentsResponse =
        client.get("${ApiRoutes.COMMENTS}/$profileName").body()
}

fun main() {
    runBlocking {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

//        val response: HttpResponse = client.get("http://127.0.0.1:5000/profile/H. Kane") {
//            contentType(ContentType.Application.Json)
//            setBody(GraphRequest(listOf(Pair("harrykane", "H. Kane"),Pair("trentarnold66", "T. Alexander-Arnold"),Pair("sterling7", "R. Sterling"))))
//        }
//        println(response.body<GraphResponse>())

        val profile = client.get("${ApiRoutes.JACCARD}").body<JaccardResponse>()
        println("$profile")
    }
}