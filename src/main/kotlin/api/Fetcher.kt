package api

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import dev.pablolec.starrylines.GetTopReposQuery
import dev.pablolec.starrylines.GetUpdateDateQuery
import dev.pablolec.starrylines.UpdateReposQuery
import models.ApiResponse
import models.Language
import models.Repository
import models.TopRepository
import mu.KotlinLogging

class Fetcher {
    private val logger = KotlinLogging.logger {}
    private val client = GraphQLClient.getClient()

    suspend fun fetchMostStarredRepos(
        language: Language,
        cursor: Optional<String>,
        maximumStars: Int?
    ): ApiResponse {
        val starsOperator = if (maximumStars != null) "<$maximumStars" else ">1"
        val query = "sort:stars stars:$starsOperator language:$language"
        val response = client.query(GetTopReposQuery(query, cursor)).execute()
        if (response.errors != null) logger.error { "Response errors: ${response.errors}" }

        val fetchedRepos = buildSet {
            response.data?.search?.edges!!.forEach {
                try {
                    add(Repository.fromEdge(it!!))
                } catch (e: Exception) {
                    logger.error { "Error while parsing edge $it: ${e.message}" }
                }
            }
        }

        return ApiResponse(
            fetchedRepos,
            response.data?.search?.pageInfo?.hasNextPage!!,
            response.data?.search?.pageInfo?.endCursor!!,
            response.data?.rateLimit
        )
    }

    suspend fun fetchReposToUpdate(repos: List<Repository>): ApiResponse {
        val response = client.query(UpdateReposQuery(repos.map { it.ghid })).execute()
        if (response.errors != null) logger.error { "Response errors: ${response.errors}" }

        val fetchedRepos = buildSet {
            response.data?.nodes!!.forEach {
                try {
                    add(Repository.fromNode(it!!))
                } catch (e: Exception) {
                    logger.error { "Error while parsing node $it: $e - ${e.message}" }
                }
            }
        }

        return ApiResponse(
            fetchedRepos,
            false,
            "",
            null
        )
    }

    suspend fun fetchUpdateDates(repos: Set<TopRepository>): Set<Pair<String, String>> {
        val response: ApolloResponse<GetUpdateDateQuery.Data>
        try {
            response = client.query(GetUpdateDateQuery(repos.map { it.ghid })).execute()
            if (response.errors != null) logger.error { "Response errors: ${response.errors}" }
        } catch (e: Exception) {
            logger.error { "Error while fetching update dates: ${e.message}" }
            return emptySet()
        }

        return buildSet {
            response.data?.nodes!!.forEach {
                try {
                    add(
                        Pair(
                            it!!.onRepository!!.id,
                            it.onRepository!!.defaultBranchRef!!.target!!.onCommit!!.pushedDate.toString()
                        )
                    )
                } catch (e: Exception) {
                    logger.error { "Error while parsing node $it: $e - ${e.message}" }
                }
            }
        }
    }
}
