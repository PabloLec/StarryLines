package api

import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloHttpException
import kotlinx.coroutines.*
import models.ApiResponse
import models.Repository
import mu.KotlinLogging

const val LIMIT_PER_LANGUAGE = 1000

class ApiManager(val languages: Set<String>) {
    private val logger = KotlinLogging.logger {}

    suspend fun run(): Map<String, Set<Repository>> {
        logger.info { "Starting to fetch repositories for languages: $languages" }
        val jobs = mutableMapOf<String, Deferred<Set<Repository>>>()

        coroutineScope {
            languages.forEach { lang ->
                jobs.put(
                    lang,
                    async {
                        fetchLanguage(lang)
                    }
                )
            }
            jobs.values.awaitAll().flatten().toSet()
        }

        return jobs.entries.associate { it.key to it.value.await() }
    }

    private suspend fun fetchLanguage(language: String): Set<Repository> {
        val fetcher = Fetcher(language)
        val repos = mutableSetOf<Repository>()
        var cursor = Optional.absent<String>()
        var failCount = 0

        while (repos.size < LIMIT_PER_LANGUAGE) {
            val apiResponse: ApiResponse
            try {
                apiResponse = fetcher.fetchMostStarredRepos(cursor)
            } catch (e: ApolloHttpException) {
                logger.debug { "$language | $e " }
                failCount++
                if (failCount >= 100) throw Exception("$language | Too many failures")
                withContext(Dispatchers.IO) {
                    Thread.sleep(5_000)
                }
                continue
            }

            repos.addAll(apiResponse.repos)
            logger.debug {
                "$language | Total repos fetched: ${repos.size}".plus(" | hasNextPage: ${apiResponse.hasNextPage}")
                    .plus(" | endCursor: ${apiResponse.endCursor}").plus(" | rateLimit: ${apiResponse.rateLimit}")
            }
            if (!apiResponse.hasNextPage) break
            cursor = Optional.present(apiResponse.endCursor)
        }

        logger.info { "$language | Fetching finished, total: ${repos.size}" }
        return repos
    }
}
