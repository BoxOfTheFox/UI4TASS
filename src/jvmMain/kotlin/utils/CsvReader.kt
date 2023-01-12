package utils

import java.io.File

class CsvReader private constructor(fileName: String){
    val rows: List<Map<String, String>>
    val headers: List<String>

    init {
        File(fileName).readLines().filter(String::isNotBlank).also {
            headers = it.first().split(',')
            rows = it.drop(1).map { headers.zip(it.split(',')).associate { it } }
        }
    }

    companion object {
        fun readFromFile(fileName: String) = CsvReader(fileName)
    }
}