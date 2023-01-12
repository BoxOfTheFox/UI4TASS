package domain

import data.GraphResponse

fun GraphResponse.toGraphState(): GraphState {
    val _nodes = nodes.mapValues { it.value.map { (it + 1) / 2 } }
    return GraphState(
        _nodes,
        edges.map {
            Edge(
                it.weight,
                Pair(
                    Node(
                        _nodes[it.nodes[0]]!![0],
                        _nodes[it.nodes[0]]!![1]
                    ),
                    Node(
                        _nodes[it.nodes[1]]!![0],
                        _nodes[it.nodes[1]]!![1]
                    )
                )
            )
        }
    )
}