package models

import dev.pablolec.starrylines.GetReposQuery

data class ApiResponse(
    val repos: Set<Repository>,
    val hasNextPage: Boolean,
    val endCursor: String,
    val rateLimit: GetReposQuery.RateLimit?
)
