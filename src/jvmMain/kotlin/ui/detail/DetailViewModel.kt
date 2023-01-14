package ui.detail

import data.ApiService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class DetailViewModel(private val name: String, private val api: ApiService) {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val profiles = MutableStateFlow<List<Pair<String, String>>?>(null)

    private val initJob = scope.launch {
        profiles.emit(api.profiles.map { it.names.zip(it.profiles) }.first())
    }

    val selectedProfile = flow { emit(api.getProfile(name)) }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    private val _similarSofifaProfiles = MutableStateFlow<List<String>>(listOf())
    val similarSofifaProfiles = _similarSofifaProfiles.asStateFlow()
    fun selectSimilarSofifa(number: Int) = scope.launch {
        initJob.join()
        _similarSofifaProfiles.emit(api.getSofifaSimilar(profiles.value!!.find { it.first == name }!!.second, number).results)
    }

    private val _similarInstaProfiles = MutableStateFlow<List<String>>(listOf())
    val similarInstaProfiles = _similarInstaProfiles.asStateFlow()
    fun selectSimilarInsta(number: Int) = scope.launch {
        initJob.join()
        _similarInstaProfiles.emit(api.getInstaSimilar(profiles.value!!.find { it.first == name }!!.second, number).results)
    }

    val comments = flow {
        initJob.join()
        emit(api.getComments(profiles.value!!.find { it.first == name }!!.second))
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    fun onDestroy() {
        scope.cancel()
    }
}