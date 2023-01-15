package domain

data class Node(
    val x: Float,
    val y: Float,
)

data class Edge(
    val weight: Float,
    val nodes: Pair<Node, Node>
)

data class GraphState(
    val nodes: Map<String, List<Float>>,
    val edges: List<Edge>
)

data class Graphs(
    val insta: GraphState,
    val sofifa: GraphState,
    val common: GraphState
)