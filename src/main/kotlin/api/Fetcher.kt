package api

import com.apollographql.apollo3.api.Optional
import dev.pablolec.starrylines.MostStarredReposQuery
import models.ApiResponse
import models.Repository
import mu.KotlinLogging

class Fetcher(val language: String) {
    private val logger = KotlinLogging.logger {}
    private val client = GraphQLClient.getClient()
    private val query = "sort:stars stars:>1 language:${language.trim().lowercase()}"

    suspend fun fetchMostStarredRepos(cursor: Optional<String>): ApiResponse {
        logger.debug { "Fetching $language with cursor: ${cursor.getOrNull()}" }
        val response = client.query(MostStarredReposQuery(query, cursor)).execute()
        if (response.errors != null) logger.error { "Response errors: ${response.errors}" }

        val fetchedRepos = mutableSetOf<Repository>()
        for (edge in response.data?.search?.edges!!) {
            fetchedRepos.add(Repository.fromEdge(edge!!))
        }

        return ApiResponse(
            fetchedRepos.toSet(),
            response.data?.search?.pageInfo?.hasNextPage!!,
            response.data?.search?.pageInfo?.endCursor!!,
            response.data?.rateLimit
        )
    }
}
