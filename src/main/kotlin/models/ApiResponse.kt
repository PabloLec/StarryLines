package models

import dev.pablolec.starrylines.GetTopReposQuery
import mu.KotlinLogging

data class ApiResponse(
    val repos: Set<Repository>,
    val hasNextPage: Boolean,
    val endCursor: String,
    val rateLimit: GetTopReposQuery.RateLimit?
) {
    init {
        if (rateLimit?.remaining != null && (rateLimit.remaining % 100 == 0 || rateLimit.remaining < 20)) {
            KotlinLogging.logger {}.info { "Remaining rate limit: ${rateLimit.remaining}" }
        }
    }
}
