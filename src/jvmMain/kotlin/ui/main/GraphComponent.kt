package ui.main

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
fun GraphComponent(
    showEdgeInfo: Boolean,
    showNodeInfo: Boolean,
    sliderPosition: Float,
    profiles: List<Pair<String, String>>,
    graphState: GraphState?,
    onClick: (String) -> Unit
) {
    var centerOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var scrollOffset by remember { mutableStateOf(1f) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }


    Column {
        Box(modifier = Modifier.scale(scrollOffset).padding(40.dp)
            .onDrag { centerOffset += it }
            .onPointerEvent(PointerEventType.Scroll) { scrollOffset -= it.changes.first().scrollDelta.y / 50 }
        ) {
            graphState?.let { graphResponse ->
                if (showNodeInfo)
                    graphResponse.nodes.forEach {
                        NodeInfo(it, profiles, canvasSize, centerOffset, onClick)
                    }
                if (showEdgeInfo)
                    graphResponse.edges.forEach { EdgeInfo(it, canvasSize, centerOffset) }
                GraphCanvas(graphResponse, { canvasSize = it }, centerOffset, sliderPosition)
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NodeInfo(
    node: Map.Entry<String, List<Float>>,
    profiles: List<Pair<String, String>>,
    canvasSize: Size,
    centerOffset: Offset,
    onClick: (String) -> Unit
) {
    var elevate by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.offset(
            LocalDensity.current.run { (canvasSize.width * node.value[0] + centerOffset.x).toDp() },
            LocalDensity.current.run { (canvasSize.height * node.value[1] + centerOffset.y).toDp() }
        ).wrapContentSize().zIndex(if (elevate) 0.9f else 0.5f)
            .clickable { onClick(profiles.find { node.key == it.second }?.first!!) }
            .onPointerEvent(PointerEventType.Enter) { elevate = true }
            .onPointerEvent(PointerEventType.Exit) { elevate = false },
        elevation = 2.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Imię: ${profiles.find { node.key == it.second }?.first}"
        )
    }
}

@Composable
fun GraphCanvas(graphState: GraphState, setCanvasSize: (Size) -> Unit, centerOffset: Offset, sliderPosition: Float) {
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
                strokeWidth = it.weight*(1+sliderPosition*10)
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