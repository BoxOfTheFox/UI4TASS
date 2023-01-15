package ui.main

import Navigation
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.onClick
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import data.GraphRequest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel, onClick: (Navigation, String) -> Unit) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    var expandedGraph by remember { mutableStateOf(false) }
    var showEdgeInfo by remember { mutableStateOf(true) }
    var showNodeInfo by remember { mutableStateOf(true) }
    var edgeWidthSliderPosition by remember { mutableStateOf(0f) }
    var edgeFilterSliderPosition by remember { mutableStateOf(0f) }

    val profiles by viewModel.profiles.collectAsState()
    val graphState by viewModel.graphState.collectAsState()
    val isChangingDate by viewModel.changingDate.collectAsState()

    Box(modifier = Modifier.onClick {
        expanded = false
        focusManager.clearFocus()
    }) {
        SearchComponent(
            expanded,
            { expanded = it },
            expandedGraph,
            { expandedGraph = it },
            showEdgeInfo,
            { showEdgeInfo = it },
            showNodeInfo,
            { showNodeInfo = it },
            edgeWidthSliderPosition,
            { edgeWidthSliderPosition = it },
            edgeFilterSliderPosition,
            { edgeFilterSliderPosition = it },
            profiles,
            graphState,
            isChangingDate,
            viewModel::setNewDate
        ) { profiles, graphSelection ->
            viewModel.buildGraph(GraphRequest(profiles, graphSelection))
        }
        GraphComponent(showEdgeInfo, showNodeInfo, edgeWidthSliderPosition, edgeFilterSliderPosition, profiles, graphState) {
            viewModel.onDestroy(); onClick(Navigation.Detail, it)
        }
    }
}