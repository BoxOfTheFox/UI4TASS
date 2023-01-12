package utils

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class Atomowki(
    val bojka: Int,
    val bajka: List<String>,
    val brawurka: List<Number>,
)
suspend fun <T> executePython(commands: List<String>, clazz: Class<T>, filePath: String) = withContext(Dispatchers.IO) {
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

