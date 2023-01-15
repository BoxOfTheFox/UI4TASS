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
import ui.main.GraphOptionsEnum

class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {

    override val profiles: Flow<Profiles> = flow { emit(client.get(ApiRoutes.PROFILES).body()) }

    override suspend fun getGraph(graphRequest: GraphRequest): GraphResponse = withContext(Dispatchers.IO) {
        client.post(graphRequest.graphType.toApiRoute()) {
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

    override suspend fun getTimeRange(startDate: String, endDate: String) {
        client.get("${ApiRoutes.GET_TIME_RANGE}/$startDate/$endDate")
    }

    private fun GraphOptionsEnum.toApiRoute() = when(this) {
        GraphOptionsEnum.Instagram -> ApiRoutes.Graph.INSTA
        GraphOptionsEnum.Sofifa -> ApiRoutes.Graph.SOFIFA
        GraphOptionsEnum.Wszystko -> ApiRoutes.Graph.GRAPH
    }
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

        val response: HttpResponse = client.post(ApiRoutes.Graph.SOFIFA) {
            contentType(ContentType.Application.Json)
            setBody(
                GraphRequest(
                    listOf(
                        Pair("H. Kane", "harrykane"),
                        Pair("T. Alexander-Arnold", "trentarnold66"),
                        Pair("R. Sterling", "sterling7")
                    ),
                    GraphOptionsEnum.Sofifa
                )
            )
        }
        println(response.body<GraphResponse>())
//        println(response.bodyAsText())

//        val profile = client.get("${ApiRoutes.GRAPH}").body<GraphResponse>()
//        println("$profile")
    }
}