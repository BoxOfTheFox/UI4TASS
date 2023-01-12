package ui

import MainViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import domain.Edge
import domain.GraphState

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun Graph(viewModel: MainViewModel) {
    var centerOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var scrollOffset by remember { mutableStateOf(1f) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    val graphState by viewModel.graphState.collectAsState()

    Column {
        Box(modifier = Modifier.scale(scrollOffset).padding(40.dp)
            .onDrag { centerOffset += it }
            .onPointerEvent(PointerEventType.Scroll) { scrollOffset -= it.changes.first().scrollDelta.y / 50 }
        ) {
            graphState?.let { graphResponse ->
                graphResponse.nodes.forEach { NodeInfo(it, viewModel.profiles.value ,canvasSize, centerOffset) }
                graphResponse.edges.forEach { EdgeInfo(it, canvasSize, centerOffset) }
                GraphCanvas(graphResponse, { canvasSize = it }, centerOffset)
            }
        }
    }
}

@Composable
fun EdgeInfo(edge: Edge, canvasSize: Size, centerOffset: Offset) {
    val y2 = canvasSize.height * edge.nodes.second.y + centerOffset.y
    val y1 = canvasSize.height * edge.nodes.first.y + centerOffset.y
    val x2 = canvasSize.width * edge.nodes.second.x + centerOffset.x
    val x1 = canvasSize.width * edge.nodes.first.x + centerOffset.x
    Box(modifier = Modifier.offset(
        LocalDensity.current.run { ((x1 + x2) / 2).toDp() - 40.dp },
        LocalDensity.current.run { ((y1 + y2) / 2).toDp() - 8.dp }
    ).wrapContentSize().padding(4.dp).background(Color.Yellow).zIndex(0.5f)
    ) {
        Text(text = "${edge.weight}")
    }
}

@Composable
fun NodeInfo(node: Map.Entry<String, List<Float>>, profiles: List<Pair<String, String>>, canvasSize: Size, centerOffset: Offset){
    var nodeShowMore by remember { mutableStateOf(false) }
    Surface(modifier = Modifier.offset(
        LocalDensity.current.run { (canvasSize.width * node.value[0] + centerOffset.x).toDp() },
        LocalDensity.current.run { (canvasSize.height * node.value[1] + centerOffset.y).toDp() }
    ).wrapContentSize().zIndex(if (nodeShowMore) 0.9f else 0.5f),
        elevation = 2.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row {
            IconButton(onClick = { nodeShowMore = !nodeShowMore }) { Icon(imageVector = Icons.Default.Info, "Home") }
            Column(modifier = Modifier.padding(top = 12.dp, end = 12.dp, bottom = 12.dp)) {
                Text(text = "Imię: ${profiles.find { node.key == it.second }?.first}")
//                if (nodeShowMore) {
//                    Text(text = "Klub: ${node.row["Club"]}")
//                    Text(text = "Wzrost: ${node.row["Height"]}")
//                }
            }
        }
    }
}

@Composable
fun GraphCanvas(graphState: GraphState, setCanvasSize: (Size) -> Unit, centerOffset: Offset) {
    Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        setCanvasSize(size)
        graphState.edges.forEach {
            drawLine(
                color = Color.Red,
                start = Offset(
                    this.size.width * it.nodes.first.x + centerOffset.x,
                    this.size.height * it.nodes.first.y + centerOffset.y
                ),
                end = Offset(
                    this.size.width * it.nodes.second.x + centerOffset.x,
                    this.size.height * it.nodes.second.y + centerOffset.y
                ),
                strokeWidth = it.weight
            )
        }

        graphState.nodes.forEach {
            drawCircle(
                color = Color.Red,
                radius = 13f,
                center = Offset(
                    this.size.width * it.value[0] + centerOffset.x,
                    this.size.height * it.value[1] + centerOffset.y
                )
            )
        }
    }
}