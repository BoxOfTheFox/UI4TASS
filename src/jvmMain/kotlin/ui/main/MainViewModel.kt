package ui.main

import data.*
import domain.Graphs
import domain.toGraphs
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class MainViewModel(private val api: ApiService) {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    val profiles = api.profiles.map { it.names.zip(it.profiles) }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )

    private val _changingDate = MutableStateFlow(false)
    val changingDate = _changingDate.asStateFlow()

    private val _graphState = MutableStateFlow<Graphs?>(null)
    val graphState = _graphState.asStateFlow()
    fun buildGraph(graphRequest: GraphRequest) = scope.launch {
        _graphState.emit(null)
        _graphState.emit(api.getGraph(graphRequest).toGraphs())
    }

    fun setNewDate(startDate: String, endDate: String) = scope.launch {
        _changingDate.emit(true)
        api.getTimeRange(startDate, endDate)
        _changingDate.emit(false)
    }

    fun onDestroy() {
        scope.cancel()
    }
}
