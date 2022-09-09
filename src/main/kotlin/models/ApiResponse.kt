package models

import dev.pablolec.starrylines.MostStarredReposQuery

data class ApiResponse(
    val repos: Set<Repository>,
    val hasNextPage: Boolean,
    val endCursor: String,
    val rateLimit: MostStarredReposQuery.RateLimit?
)
