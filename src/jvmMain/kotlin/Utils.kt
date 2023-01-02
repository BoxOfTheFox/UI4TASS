import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class Atomowki(
    val bojka: Int,
    val bajka: List<String>,
    val brawurka: List<Number>,
)
suspend fun <T>executePython(commands: List<String>, clazz: Class<T>, filePath: String) =
    withContext(Dispatchers.IO) {
        ProcessBuilder(listOf("python3", *commands.toTypedArray()))
            .apply { directory(File(filePath)) }
            .start().inputStream.reader().readText().let {
                Gson().fromJson(it, clazz)
            }
    }

suspend fun executePython(commands: List<String>, filePath: String) =
    executePython(commands, Map::class.java, filePath)

suspend fun executePython(command: String, filePath: String) =
    executePython(listOf(command), Map::class.java, filePath)

suspend fun <T>executePython(command: String, clazz: Class<T>, filePath: String) =
    executePython(listOf(command), clazz, filePath)

suspend fun readCsv(fileName: String): Map<String, List<String>> = withContext(Dispatchers.IO) {
    val allLines = File(fileName).readLines().filter(String::isNotBlank)
    if (allLines.isEmpty()) return@withContext mapOf()

    val headers = allLines.first().split(',')
    allLines.drop(1)
        .map { it.split(',') }
        .map { headers.zip(it).associate { it } }
        .flatMap { it.entries }.groupBy({ it.key }) { it.value }
}

data class Row(
    val height: String,
    val name: String,
    val country: String,
    val club: String
)

fun Map<String, List<String>>.toRows() = (0 until (this["Height"]?.size ?: 0)).map {
    Row(
        this["Height"]?.get(it) ?: "",
        this["Name"]?.get(it) ?: "",
        this["Country"]?.get(it) ?: "",
        this["Club"]?.get(it) ?: ""
    )
}