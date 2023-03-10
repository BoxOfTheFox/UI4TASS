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
import domain.Graphs
import ui.ScrollableLazyRow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchComponent(
    expandedPlayerDropDown: Boolean,
    setExpandedPlayerDropDown: (Boolean) -> Unit,
    expandedGraphDropDown: Boolean,
    setExpandedGraphDropDown: (Boolean) -> Unit,
    isEdgeChecked: Boolean,
    setEdgeChecked: (Boolean) -> Unit,
    isNodeChecked: Boolean,
    setNodeChecked: (Boolean) -> Unit,
    sliderPosition: Float,
    setSliderPosition: (Float) -> Unit,
    filterSliderPosition: Float,
    setFilterSliderPosition: (Float) -> Unit,
    profiles: List<Pair<String, String>>,
    graphs: Graphs?,
    isChangingDate: Boolean,
    onNewTimeClick: (String, String) -> Unit,
    onClick: (List<Pair<String, String>>, GraphOptionsEnum) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    val selectedItems = remember { mutableStateListOf<Pair<String, String>>() }
    var settingsVisible by remember { mutableStateOf(true) }
    val graphOptions = GraphOptionsEnum.values()
    var graphSelection by remember { mutableStateOf(GraphOptionsEnum.Instagram) }
    var startDate by remember { mutableStateOf("2000.11.1") }
    var endDate by remember { mutableStateOf(LocalDate.now().format(formatter)) }

    Row(modifier = Modifier.padding(16.dp).zIndex(2f)) {
        Box(modifier = Modifier.weight(1f)) {
            if (!settingsVisible)
                IconButton(onClick = { settingsVisible = true }) {
                    Icon(imageVector = Icons.Default.Settings, "Settings")
                }
        }
        Column(modifier = Modifier.weight(5f)) {
            if (settingsVisible) {
                Surface(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                        IconButton(
                            modifier = Modifier.offset(y = 4.dp),
                            onClick = { settingsVisible = false }
                        ) {
                            Icon(imageVector = Icons.Default.Close, "Minimize")
                        }
                        if (isChangingDate)
                            Box(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.weight(3f),
                                    value = startDate,
                                    onValueChange = { startDate = it },
                                    label = { Text("Data pocz??tkowa", maxLines = 1) },
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    modifier = Modifier.weight(3f),
                                    value = endDate,
                                    onValueChange = { endDate = it },
                                    label = { Text("Data ko??cowa", maxLines = 1) },
                                    singleLine = true
                                )
                                IconButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = { onNewTimeClick(startDate, endDate) }) {
                                    Icon(imageVector = Icons.Default.Build, "Wybierz przedzia?? czasowy")
                                }
                            }
                            if (selectedItems.isNotEmpty())
                                ScrollableLazyRow(selectedItems) {
                                    Chip(onClick = { selectedItems.remove(it) }) { Text(it.first) }
                                }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                DropdownMenuBox(
                                    modifier = Modifier.weight(3f),
                                    expandedPlayerDropDown,
                                    setExpandedPlayerDropDown,
                                    selectedItems,
                                    profiles
                                ) { onClick(selectedItems, graphSelection) }
                                SimpleDropdownMenuBox(
                                    modifier = Modifier.weight(2f),
                                    expandedGraphDropDown,
                                    setExpandedGraphDropDown,
                                    graphSelection,
                                    { graphSelection = it },
                                    graphOptions
                                )
                            }
                            if (graphs != null) {
                                Column {
                                    Row(
                                        modifier = Modifier.padding(top = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isEdgeChecked,
                                            onCheckedChange = { setEdgeChecked(it) })
                                        Text("Poka?? wagi")

                                        Checkbox(
                                            modifier = Modifier.padding(start = 8.dp),
                                            checked = isNodeChecked,
                                            onCheckedChange = { setNodeChecked(it) })
                                        Text("Poka?? pi??karzy")
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(modifier = Modifier.padding(end = 4.dp), text = "Grubo???? kraw??dzi")
                                        Slider(value = sliderPosition, onValueChange = { setSliderPosition(it) })
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(modifier = Modifier.padding(end = 4.dp), text = "Filtr kraw??dzi")
                                        Slider(value = filterSliderPosition, onValueChange = { setFilterSliderPosition(it) })
                                    }
                                }
                            }
                        }
                    }
                }
                if (graphs == null && !isChangingDate)
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        Box(modifier = Modifier.weight(1f))
    }
}

@Composable
fun DropdownMenuBox(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    selectedItems: SnapshotStateList<Pair<String, String>>,
    profiles: List<Pair<String, String>>,
    onClickSearch: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var rowSize by remember { mutableStateOf(Size.Zero) }

    Box(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.wrapContentHeight().fillMaxWidth()
                .onGloballyPositioned { rowSize = it.size.toSize() }
                .onFocusChanged { setExpanded(it.isFocused) },
            value = input,
            onValueChange = { input = it },
            label = { Text("Podaj nick lub nazwisko pi??karza", maxLines = 1) },
            leadingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    "Poka?? opcje"
                )
            },
            trailingIcon = {
                IconButton(onClick = { onClickSearch() }) {
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
            profiles.toMutableList().apply { removeAll(selectedItems) }
                .filter { input in it.first || input in it.second }.forEach {
                DropdownMenuItem(
                    onClick = { selectedItems.add(it) }
                ) { Text(text = "${it.first} (${it.second})") }
            }
        }
    }
}

@Composable
fun SimpleDropdownMenuBox(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    selection: GraphOptionsEnum,
    setSelection: (GraphOptionsEnum) -> Unit,
    options: Array<GraphOptionsEnum>
) {
    var rowSize by remember { mutableStateOf(Size.Zero) }

    Box(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.wrapContentHeight().fillMaxWidth()
                .onGloballyPositioned { rowSize = it.size.toSize() }
                .onFocusChanged { setExpanded(it.isFocused) },
            value = selection.name,
            onValueChange = {},
            label = { Text("Wybierz zbi??r", maxLines = 1) },
            leadingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    "Poka?? opcje"
                )
            },
            singleLine = true,
            readOnly = true
        )
        DropdownMenu(
            modifier = Modifier.width(LocalDensity.current.run { rowSize.width.toDp() }),
            expanded = expanded,
            focusable = false,
            onDismissRequest = {}
        ) {
            options.forEach {
                DropdownMenuItem(onClick = { setSelection(it) }) { Text(it.name) }
            }
        }
    }
}

enum class GraphOptionsEnum {
    Instagram, Sofifa, Wszystko
}

fun main() {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    println(LocalDate.now().format(formatter))
}