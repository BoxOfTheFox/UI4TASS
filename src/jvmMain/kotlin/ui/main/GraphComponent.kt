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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import domain.Edge
import domain.GraphState
import domain.Graphs
import domain.Node

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun GraphComponent(
    showEdgeInfo: Boolean,
    showNodeInfo: Boolean,
    sliderPosition: Float,
    filterSliderPosition: Float,
    profiles: List<Pair<String, String>>,
    graphs: Graphs?,
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
            graphs?.let { graphs ->
                if (showNodeInfo) {
                    graphs.insta.nodes.forEach { NodeInfo(it, profiles, canvasSize, centerOffset, onClick) }
                    graphs.sofifa.nodes.forEach { NodeInfo(it, profiles, canvasSize, centerOffset, onClick) }
                    graphs.common.nodes.forEach { NodeInfo(it, profiles, canvasSize, centerOffset, onClick) }
                }
                if (showEdgeInfo) {
                    graphs.insta.edges.forEach { EdgeInfo(it, canvasSize, centerOffset) }
                    graphs.sofifa.edges.forEach { EdgeInfo(it, canvasSize, centerOffset) }
                    graphs.common.edges.forEach { EdgeInfo(it, canvasSize, centerOffset) }
                }
                GraphCanvas(graphs, { canvasSize = it }, centerOffset, sliderPosition, filterSliderPosition)
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
            text = "Imi??: ${profiles.find { node.key == it.second }?.first}"
        )
    }
}

@Composable
fun GraphCanvas(graphs: Graphs, setCanvasSize: (Size) -> Unit, centerOffset: Offset, sliderPosition: Float, filterSliderPosition: Float) {
    Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        setCanvasSize(size)
        graphs.insta.edges
            .filterNot { it in graphs.common.edges }
            .filter { it.weight > filterSliderPosition }
            .forEach { drawEdge(it, centerOffset, sliderPosition, Color.Blue) }
        graphs.sofifa.edges
            .filterNot { it in graphs.common.edges }
            .filter { it.weight > filterSliderPosition }
            .forEach { drawEdge(it, centerOffset, sliderPosition, Color.Green) }
        graphs.common.edges.forEach { drawEdge(it, centerOffset, sliderPosition, Color.Gray) }

        graphs.insta.nodes.filterNot { it in graphs.common.nodes.entries }.forEach { drawNode(it, centerOffset, Color.Blue) }
        graphs.sofifa.nodes.filterNot { it in graphs.common.nodes.entries }.forEach { drawNode(it, centerOffset, Color.Green) }
        graphs.common.nodes.forEach { drawNode(it, centerOffset, Color.Gray) }
    }
}

fun DrawScope.drawEdge(edge: Edge, centerOffset: Offset, sliderPosition: Float, color: Color) {
    drawLine(
        color = color,
        start = Offset(
            this.size.width * edge.nodes.first.x + centerOffset.x,
            this.size.height * edge.nodes.first.y + centerOffset.y
        ),
        end = Offset(
            this.size.width * edge.nodes.second.x + centerOffset.x,
            this.size.height * edge.nodes.second.y + centerOffset.y
        ),
        strokeWidth = edge.weight*(1+sliderPosition*10)
    )
}

fun DrawScope.drawNode(node: Map.Entry<String, List<Float>>, centerOffset: Offset, color: Color) {
    drawCircle(
        color = color,
        radius = 13f,
        center = Offset(
            this.size.width * node.value[0] + centerOffset.x,
            this.size.height * node.value[1] + centerOffset.y
        )
    )
}