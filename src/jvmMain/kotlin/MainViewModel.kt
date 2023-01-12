import data.ApiService
import data.GraphRequest
import data.GraphResponse
import data.Profiles
import domain.GraphState
import domain.toGraphState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import utils.Atomowki
import utils.CsvReader
import utils.executePython
import kotlin.random.Random


class MainViewModel(private val api: ApiService = ApiService.create()) {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val path = System.getProperty("user.dir")
    private val target = "$path/src/jvmMain/resources/raw"

    val profiles = api.profiles.map { it.names.zip(it.profiles) }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )

    private val _graphState = MutableStateFlow<GraphState?>(null)
    val graphState = _graphState.asStateFlow()
    fun buildGraph(graphRequest: GraphRequest) {
        scope.launch {
            _graphState.emit(null)
            _graphState.emit(api.getGraph(graphRequest).toGraphState())
        }
    }

    private val _testState = MutableStateFlow("")
    val testState = _testState.asStateFlow()

    private var csvReader: CsvReader? = null
    private val csvReaderJob = scope.launch {
        withContext(Dispatchers.IO) {
            csvReader = CsvReader.readFromFile("$target/sofifa_processed.csv")
        }
    }

    fun test(vararg commands: String) {
        scope.launch {
            _testState.emit("Ładuję…")
            _testState.emit(executePython(commands.toList(), Atomowki::class.java, target).toString())
//        utils.executePython("file.py", utils.Atomowki::class.java)
//        utils.executePython(listOf("file.py", "u", "a"), utils.Atomowki::class.java, target)
//        utils.executePython("file.py")
//        utils.executePython(listOf("file.py", "u", "a"))
        }
    }

    fun clearTest() {
        scope.launch {
            _testState.emit("")
        }
    }

    val sofifaState = flow {
        csvReaderJob.join()
        emit(csvReader!!.rows)
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )

    fun onDestroy() {
        scope.cancel()
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

        val response: HttpResponse = client.get("http://127.0.0.1:5000/graph") {
            contentType(ContentType.Application.Json)
            setBody(GraphRequest(listOf(Pair("harrykane", "H. Kane"),Pair("trentarnold66", "T. Alexander-Arnold"),Pair("sterling7", "R. Sterling"))))
        }
        println(response.body<GraphResponse>())

//        val profiles = client.get("http://127.0.0.1:5000/graph").body<GraphResponse>()
//        println("$profiles")
    }
}