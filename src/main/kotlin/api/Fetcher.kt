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

    suspend fun fetchMostStarredRepos(language: String, cursor: Optional<String>): ApiResponse {
        logger.debug { "$language | Fetching with cursor: ${cursor.getOrNull()}" }
        val query = "sort:stars stars:>1 language:${language.trim().lowercase()}"
        val response = client.query(GetTopReposQuery(query, cursor)).execute()
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

    suspend fun fetchReposToUpdate(repos: List<Repository>): ApiResponse {
        val response = client.query(UpdateReposQuery(repos.map { it.ghid })).execute()
        if (response.errors != null) logger.error { "Response errors: ${response.errors}" }

        val fetchedRepos = mutableSetOf<Repository>()
        for (node in response.data?.nodes!!) {
            fetchedRepos.add(Repository.fromNode(node!!))
        }

        return ApiResponse(
            fetchedRepos.toSet(),
            false,
            "",
            null
        )
    }
}
