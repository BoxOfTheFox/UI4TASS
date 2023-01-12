package data

import kotlinx.serialization.Serializable

@Serializable
data class EdgePojo(
    val weight: Float,
    val nodes: List<String>
)

@Serializable
data class GraphRequest(
    val profiles: List<Pair<String, String>>
)

@Serializable
data class GraphResponse(
    val nodes: Map<String, List<Float>>,
    val edges: List<EdgePojo>
)

@Serializable
data class Profiles(
    val profiles: List<String>,
    val names: List<String>,
)