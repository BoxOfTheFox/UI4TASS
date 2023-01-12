package data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {

//    override suspend fun createProducts(productRequest: RequestModel): ResponseModel? {
//        return try {
//
//            client.post<ResponseModel> {
//                url(ApiRoutes.PRODUCTS)
//                body = productRequest
//            }
//        } catch (ex: RedirectResponseException) {
//            // 3xx - responses
//            println("Error: ${ex.response.status.description}")
//            null
//        } catch (ex: ClientRequestException) {
//            // 4xx - responses
//            println("Error: ${ex.response.status.description}")
//            null
//        } catch (ex: ServerResponseException) {
//            // 5xx - response
//            println("Error: ${ex.response.status.description}")
//            null
//        }
//    }

    override val profiles: Flow<Profiles> = flow { emit(client.get(ApiRoutes.PROFILES).body()) }

    override suspend fun getGraph(graphRequest: GraphRequest): GraphResponse = withContext(Dispatchers.IO) {
        client.get(ApiRoutes.GRAPH) {
            contentType(ContentType.Application.Json)
            setBody(graphRequest)
        }.body()
    }
}