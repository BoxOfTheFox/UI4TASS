import java.io.File

fun readCsv(fileName: String) = File(fileName).readLines().filter(String::isNotBlank).drop(1).map {
    val row = it.split(',', ignoreCase = false)
    Row(row[row.size-4].toInt(), row[row.size-3], row[row.size-2], row[row.size-1])
}

data class Row(
    val height: Int,
    val name: String,
    val country: String,
    val club: String
)