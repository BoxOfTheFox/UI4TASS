package data

object ApiRoutes {
    private const val BASE_URL = "http://127.0.0.1:5000"
    const val PROFILES = "$BASE_URL/profiles"
    const val PROFILE = "$BASE_URL/profile"

    object Graph {
        const val GRAPH = "$BASE_URL/graph"
        const val SOFIFA = "$GRAPH/sofifa"
        const val INSTA = "$GRAPH/instagram"
    }

    object Similar {
        private const val SMILIAR = "/similar"
        const val INSTA = "$BASE_URL/$SMILIAR/insta"
        const val SOFIFA = "$BASE_URL/$SMILIAR/sofifa"
    }

    const val COMMENTS = "$BASE_URL/comments"
    const val JACCARD = "$BASE_URL/jaccard"
    const val GET_TIME_RANGE = "$BASE_URL/get_time_range"
}