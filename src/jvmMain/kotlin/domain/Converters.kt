package domain

import data.GraphResponse

fun GraphResponse.toGraphs(): Graphs {
    val instaNodes = nodes.instagram_only.fixNodes()
    val sofifaNodes = nodes.sofifa_only.fixNodes()
    val commonNodes = nodes.common.fixNodes()

    return Graphs(
        insta = GraphState(instaNodes, edges.instagram_only.zip(weights.ifEmpty { List(edges.instagram_only.size) { 1f } }).toEdge(instaNodes)),
        sofifa = GraphState(sofifaNodes, edges.sofifa_only.zip(weights.ifEmpty { List(edges.sofifa_only.size) { 1f } }).toEdge(sofifaNodes)),
        common = GraphState(commonNodes, edges.common.zip(List(edges.common.size) {1f}).toEdge(commonNodes)),
    )
}

private fun List<Pair<List<String>, Float>>.toEdge(nodes: Map<String, List<Float>>) = map {
    Edge(
        it.second,
        Node(nodes[it.first[0]]!![0], nodes[it.first[0]]!![1]) to Node(nodes[it.first[1]]!![0], nodes[it.first[1]]!![1])
    )
}

private fun List<Map<String, List<Float>>>.fixNodes() = associate {
    it.keys.first() to it.values.first().map { (it + 1) / 2 }
}