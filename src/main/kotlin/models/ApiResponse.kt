package models

import dev.pablolec.starrylines.GetTopReposQuery

data class ApiResponse(
    val repos: Set<Repository>,
    val hasNextPage: Boolean,
    val endCursor: String,
    val rateLimit: GetTopReposQuery.RateLimit?
)
