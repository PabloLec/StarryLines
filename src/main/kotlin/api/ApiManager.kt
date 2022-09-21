package api

import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloHttpException
import db.MongoManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import models.ApiResponse
import models.Repository
import mu.KotlinLogging
import java.time.LocalDateTime

const val LIMIT_PER_LANGUAGE = 1000

class ApiManager(private val mongoManager: MongoManager, val languages: Set<String>) {
    private val logger = KotlinLogging.logger {}

    suspend fun run() {
        val languagesMap = fetchTopRepos()
        mongoManager.updateAll(languagesMap)

        val toUpdate = mongoManager
            .getAllRepos(languages)
            .filter { it.second.githubUpdateDate < LocalDateTime.now().minusDays(1) }
            .groupBy { it.first }
            .mapValues { it.value.map { it.second } }

        val updatedMap = updateRepos(toUpdate)
        mongoManager.updateAll(updatedMap)
    }

    private suspend fun fetchTopRepos(): Map<String, Set<Repository>> {
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
        val fetcher = Fetcher()
        val repos = mutableSetOf<Repository>()
        var cursor = Optional.absent<String>()

        while (repos.size < LIMIT_PER_LANGUAGE) {
            val apiResponse: ApiResponse
            try {
                apiResponse = fetcher.fetchMostStarredRepos(language, cursor)
            } catch (e: ApolloHttpException) {
                logger.debug { " $language | $e " }
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

    private suspend fun updateRepos(toUpdate: Map<String, List<Repository>>): Map<String, Set<Repository>> {
        logger.info { "Starting to update repositories for languages: $languages with ${toUpdate.flatMap { it.value }.size} repos" }
        val jobs = mutableMapOf<String, Deferred<Set<Repository>>>()

        coroutineScope {
            toUpdate.forEach { (lang, repos) ->
                jobs.put(
                    lang,
                    async {
                        updateLanguage(repos)
                    }
                )
            }
            jobs.values.awaitAll().flatten().toSet()
        }

        return jobs.entries.associate { it.key to it.value.await() }
    }

    private suspend fun updateLanguage(repos: List<Repository>): Set<Repository> {
        val fetcher = Fetcher()
        val updatedRepos = mutableSetOf<Repository>()

        repos.chunked(20).forEach {
            val apiResponse: ApiResponse
            try {
                apiResponse = fetcher.fetchReposToUpdate(it)
                updatedRepos.addAll(apiResponse.repos)
            } catch (e: Exception) {
                logger.debug { " $it | $e " }
            }
        }

        return updatedRepos
    }
}
