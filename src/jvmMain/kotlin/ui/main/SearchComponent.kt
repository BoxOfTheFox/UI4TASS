package ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import domain.GraphState
import ui.ScrollableLazyRow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchComponent(
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    isEdgeChecked: Boolean,
    setEdgeChecked: (Boolean) -> Unit,
    isNodeChecked: Boolean,
    setNodeChecked: (Boolean) -> Unit,
    sliderPosition: Float,
    setSliderPosition: (Float) -> Unit,
    profiles: List<Pair<String, String>>,
    graphState: GraphState?,
    onClick: (List<Pair<String, String>>) -> Unit
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
                            ScrollableLazyRow(selectedItems) {
                                Chip(onClick = { selectedItems.remove(it) }) { Text(it.first) }
                            }
                        DropdownMenuBox(expanded, setExpanded, selectedItems, profiles) { onClick(it) }
                        if(graphState != null) {
                            Column {
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isEdgeChecked,
                                        onCheckedChange = { setEdgeChecked(it) })
                                    Text("Pokaż wagi")

                                    Checkbox(
                                        modifier = Modifier.padding(start=8.dp),
                                        checked = isNodeChecked,
                                        onCheckedChange = { setNodeChecked(it) })
                                    Text("Pokaż piłkarzy")
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(modifier = Modifier.padding(end=4.dp), text = "Grubość krawędzi")
                                    Slider(value = sliderPosition, onValueChange = { setSliderPosition(it) })
                                }
                            }
                        }
                    }
                }
                if (graphState == null)
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        Box(modifier = Modifier.weight(1f))
    }
}

@Composable
fun DropdownMenuBox(
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    selectedItems: SnapshotStateList<Pair<String, String>>,
    profiles: List<Pair<String, String>>,
    onBuildGraph: (List<Pair<String, String>>) -> Unit
) {
    var input by remember { mutableStateOf("") }
    var rowSize by remember { mutableStateOf(Size.Zero) }

    Box {
        OutlinedTextField(
            modifier = Modifier.wrapContentHeight().fillMaxWidth()
                .onGloballyPositioned { rowSize = it.size.toSize() }
                .onFocusChanged { setExpanded(it.isFocused) },
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
                    Icon(imageVector = Icons.Default.Search, "Buduj graf")
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
            profiles.toMutableList().apply { removeAll(selectedItems) }.filter { input in it.first || input in it.second }.forEach {
                DropdownMenuItem(
                    onClick = { selectedItems.add(it) }
                ) { Text(text = "${it.first} (${it.second})") }
            }
        }
    }
}