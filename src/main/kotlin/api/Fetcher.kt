package api

import com.apollographql.apollo3.api.Optional
import dev.pablolec.starrylines.GetTopReposQuery
import dev.pablolec.starrylines.UpdateReposQuery
import models.ApiResponse
import models.Repository
import mu.KotlinLogging

class Fetcher {
    private val logger = KotlinLogging.logger {}
    private val client = GraphQLClient.getClient()

    suspend fun fetchMostStarredRepos(language: String, cursor: Optional<String>, maximumStars: Int?): ApiResponse {
        val starsOperator = if (maximumStars != null) "<$maximumStars" else ">1"
        val query = "sort:stars stars:$starsOperator language:${language.trim().lowercase()}"
        val response = client.query(GetTopReposQuery(query, cursor)).execute()
        if (response.errors != null) logger.error { "Response errors: ${response.errors}" }

        val fetchedRepos = mutableSetOf<Repository>()
        for (edge in response.data?.search?.edges!!) {
            try {
                fetchedRepos.add(Repository.fromEdge(edge!!))
            } catch (e: Exception) {
                logger.error { "Error while parsing edge $edge: ${e.message}" }
            }
        }

        return ApiResponse(
            fetchedRepos.toSet(),
            response.data?.search?.pageInfo?.hasNextPage!!,
            response.data?.search?.pageInfo?.endCursor!!,
            response.data?.rateLimit
        )
    }

    suspend fun fetchReposToUpdate(repos: List<Repository>): ApiResponse {
        val response = client.query(UpdateReposQuery(repos.map { it.ghid })).execute()
        if (response.errors != null) logger.error { "Response errors: ${response.errors}" }

        val fetchedRepos = mutableSetOf<Repository>()
        for (node in response.data?.nodes!!) {
            try {
                fetchedRepos.add(Repository.fromNode(node!!))
            } catch (e: Exception) {
                logger.error { "Error while parsing node $node: ${e.message}" }
            }
        }

        return ApiResponse(
            fetchedRepos.toSet(),
            false,
            "",
            null
        )
    }
}
