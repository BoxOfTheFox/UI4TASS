package domain

import data.Edges
import data.GraphResponse
import data.Nodes
import ui.main.GraphOptionsEnum

fun GraphResponse.toGraphState(optionsEnum: GraphOptionsEnum): GraphState {
    val _nodes = nodes.toGraphStateNode(optionsEnum)!!.associate {
        it.keys.first() to it.values.first().map { (it + 1) / 2 }
    }
    return GraphState(
        _nodes,
        edges.toGraphStateEdge(optionsEnum)!!.map {
            Edge(
                1f,
                Node(
                    _nodes[it[0]]!![0],
                    _nodes[it[0]]!![1]
                ) to Node(
                    _nodes[it[1]]!![0],
                    _nodes[it[1]]!![1]
                )
            )
        }
    )
}

fun Nodes.toGraphStateNode(optionsEnum: GraphOptionsEnum) = when(optionsEnum) {
    GraphOptionsEnum.Instagram -> instagram_only
    GraphOptionsEnum.Sofifa -> sofifa_only
    GraphOptionsEnum.Wszystko -> common
}

fun Edges.toGraphStateEdge(optionsEnum: GraphOptionsEnum) = when(optionsEnum) {
    GraphOptionsEnum.Instagram -> instagram_only
    GraphOptionsEnum.Sofifa -> sofifa_only
    GraphOptionsEnum.Wszystko -> common
}