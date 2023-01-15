package data

import kotlinx.serialization.Serializable
import ui.main.GraphOptionsEnum

@Serializable
data class GraphRequest(
    val profiles: List<Pair<String, String>>,
    val graphType: GraphOptionsEnum
)

@Serializable
data class GraphResponse(
    val edges: Edges,
    val nodes: Nodes,
    val weights: List<Float> = listOf()
)

@Serializable
data class Edges(
    val common: List<List<String>> = listOf(),
    val instagram_only: List<List<String>> = listOf(),
    val sofifa_only: List<List<String>> = listOf()
)

@Serializable
data class Nodes(
    val common: List<Map<String,List<Float>>> = listOf(),
    val instagram_only: List<Map<String,List<Float>>> = listOf(),
    val sofifa_only: List<Map<String,List<Float>>> = listOf()
)

@Serializable
data class SimilarityResponse(
    val results: List<String>
)

@Serializable
data class Profiles(
    val profiles: List<String>,
    val names: List<String>,
)

@Serializable
data class CommentsResponse(
    val player: String,
    val comments: List<String>,
    val keywords: List<String>
)

@Serializable
data class JaccardResponse(
    val jaccard_index: Float
)

@Serializable
data class Profile(
    val Overall: Int,
    val Potential: Int,
    val Weak_foot: Int,
    val Skill_Moves: Int,
    val Value: Float,
    val Salary: Int,
    val Crossing: Int,
    val Finishing: Int,
    val Head_accuracy: Int,
    val Short_passing: Int,
    val Volley: Int,
    val Dribbling: Int,
    val Effect: Int,
    val Precision: Int,
    val Long_passing: Int,
    val Control: Int,
    val Acceleration: Int,
    val Speed: Int,
    val Agility: Int,
    val Reactions: Int,
    val Balance: Int,
    val Hit_power: Int,
    val Relaxation: Int,
    val Endurance: Int,
    val Strength: Int,
    val Long_shots: Int,
    val Aggressiveness: Int,
    val Interceptions: Int,
    val Placement_mental: Int,
    val Vision: Int,
    val Penalty: Int,
    val Calmness: Int,
    val Defensive_awareness: Int,
    val Standing_tackle: Int,
    val Tackle_sliding: Int,
    val Diving: Int,
    val Hand_game: Int,
    val Foot_game: Int,
    val Placement_GK: Int,
    val Reflexes: Int,
    val Preferred_foot_Left: Int,
    val Preferred_foot_Right: Int,
    val Offensive_performance_High: Int,
    val Offensive_performance_Low: Int,
    val Offensive_performance_Medium: Int,
    val Defensive_performance_High: Int,
    val Defensive_performance_Low: Int,
    val Defensive_performance_Medium: Int,
    val Height: Int,
    val Id: Int,
    val Name: String,
    val Country: String,
    val Club: String
)