package ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun SearchComponent(
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    profiles: StateFlow<List<Pair<String, String>>>,
    isLoading: Boolean,
    onBuildGraph: (List<Pair<String, String>>) -> Unit
) {
    val selectedItems = remember { mutableStateListOf<Pair<String, String>>() }
    var settingsVisible by remember { mutableStateOf(true) }

    Row(modifier = Modifier.padding(16.dp).zIndex(2f)) {
        Box(modifier = Modifier.weight(1f)) {
            if (!settingsVisible)
                IconButton(onClick = { settingsVisible = true }) {
                    Icon(imageVector = Icons.Default.Settings, "Settings")
                }
        }
        Column(modifier = Modifier.weight(3f)) {
            if (settingsVisible) {
                Surface(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                        IconButton(
                            modifier = Modifier.offset(y = 4.dp),
                            onClick = { settingsVisible = false }
                        ) {
                            Icon(imageVector = Icons.Default.Close, "Minimize")
                        }
                        if (selectedItems.isNotEmpty())
                            ChipLazyRow(selectedItems)
                        DropdownMenuBox(expanded, setExpanded, selectedItems, profiles, onBuildGraph)
                    }
                }
                if (isLoading)
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        Box(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChipLazyRow(selectedItems: SnapshotStateList<Pair<String, String>>) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyRow(
        state = scrollState,
        modifier = Modifier.draggable(
            orientation = Orientation.Horizontal,
            state = rememberDraggableState { delta ->
                coroutineScope.launch {
                    scrollState.scrollBy(-delta)
                }
            }
        ).onPointerEvent(PointerEventType.Scroll) {
            coroutineScope.launch {
                scrollState.scrollBy(it.changes.first().scrollDelta.y*10)
            }
        },
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(selectedItems) {
            Chip(onClick = { selectedItems.remove(it) }) { Text(it.first) }
        }
    }
}

@Composable
fun DropdownMenuBox(
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    selectedItems: SnapshotStateList<Pair<String, String>>,
    profiles: StateFlow<List<Pair<String, String>>>,
    onBuildGraph: (List<Pair<String, String>>) -> Unit
) {
    var input by remember { mutableStateOf("") }
    var rowSize by remember { mutableStateOf(Size.Zero) }
    val listItems = profiles.collectAsState()

    val focusRequester = remember { FocusRequester() }

    Box {
        OutlinedTextField(
            modifier = Modifier.wrapContentHeight().fillMaxWidth()
                .onGloballyPositioned { rowSize = it.size.toSize() }
                .onFocusChanged { setExpanded(it.isFocused) }
                .focusRequester(focusRequester),
            value = input,
            onValueChange = { input = it },
            label = { Text("Podaj nick lub nazwisko piłkarza") },
            leadingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    "Pokaż opcje"
                )
            },
            trailingIcon = {
                IconButton(onClick = { onBuildGraph(selectedItems) }) {
                    Icon(imageVector = Icons.Default.Build, "Buduj graf")
                }
            },
            singleLine = true
        )
        DropdownMenu(
            modifier = Modifier
                .width(LocalDensity.current.run { rowSize.width.toDp() })
                .requiredSizeIn(maxHeight = 300.dp),
            expanded = expanded,
            focusable = false,
            onDismissRequest = {}
        ) {
            listItems.value.toMutableList().apply { removeAll(selectedItems) }.filter { input in it.first || input in it.second }.forEach {
                DropdownMenuItem(
                    onClick = { selectedItems.add(it) }
                ) { Text(text = "${it.first} (${it.second})") }
            }
        }
    }
}