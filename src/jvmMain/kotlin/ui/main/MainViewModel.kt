package ui.main

import data.*
import domain.GraphState
import domain.toGraphState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class MainViewModel(private val api: ApiService) {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    val profiles = api.profiles.map { it.names.zip(it.profiles) }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )

    private val _graphState = MutableStateFlow<GraphState?>(null)
    val graphState = _graphState.asStateFlow()
    fun buildGraph(graphRequest: GraphRequest) = scope.launch {
        _graphState.emit(null)
        _graphState.emit(api.getGraph(graphRequest).toGraphState())
    }

    fun onDestroy() {
        scope.cancel()
    }
}
