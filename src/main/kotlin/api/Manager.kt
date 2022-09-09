package api

import com.apollographql.apollo3.api.Optional
import models.Repository
import mu.KotlinLogging

const val LIMIT_PER_LANGUAGE = 1000

class Manager(val language: String) {
    private val logger = KotlinLogging.logger {}
    private val fetcher = Fetcher(language)
    private var cursor = Optional.absent<String>()

    suspend fun run() {
        // TODO: Implement multiple languages support
        fetchLanguage(language)
    }

    private suspend fun fetchLanguage(language: String) {
        val repos = mutableSetOf<Repository>()

        while (repos.size < LIMIT_PER_LANGUAGE) {
            val apiResponse = fetcher.fetchMostStarredRepos(cursor)

            repos.addAll(apiResponse.repos)
            logger.debug {
                "Total repos fetched for $language: ${repos.size}".plus(" | hasNextPage: ${apiResponse.hasNextPage}")
                    .plus(" | endCursor: ${apiResponse.endCursor}").plus(" | rateLimit: ${apiResponse.rateLimit}")
            }
            if (!apiResponse.hasNextPage) break
            cursor = Optional.present(apiResponse.endCursor)
        }

        logger.info { "Finished fetching for $language}, total: ${repos.size}" }
    }
}
