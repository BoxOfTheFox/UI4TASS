import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class MainViewModel {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val path = System.getProperty("user.dir")
    private val target = "$path/src/jvmMain/resources/raw"

    private val _testState = MutableStateFlow("")
    val testState = _testState.asStateFlow()

    fun test(vararg commands: String) {
        scope.launch {
            _testState.emit("Ładuję…")
            _testState.emit(executePython(commands.toList(), Atomowki::class.java, target).toString())
//        executePython("file.py", Atomowki::class.java)
//        executePython(listOf("file.py", "u", "a"), Atomowki::class.java, target)
//        executePython("file.py")
//        executePython(listOf("file.py", "u", "a"))
        }
    }

    fun clearTest() {
        scope.launch {
            _testState.emit("")
        }
    }

    val sofifaState = flow {
        emit(readCsv("$target/sofifa_processed.csv"))
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = mapOf()
    )

    fun onDestroy() {
        scope.cancel()
    }
}
