package api

import com.apollographql.apollo3.api.Optional
import db.MongoManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import models.ApiResponse
import models.Language
import models.Repository
import mu.KotlinLogging
import java.time.LocalDateTime

private const val MINIMUM_STARS = 200

class ApiManager(private val mongoManager: MongoManager, var languages: Set<Language>) {
    private val logger = KotlinLogging.logger {}
    private val minimumStarsFound = mutableMapOf<Language, Int>()

    suspend fun run() {
        languages = languages.shuffled().toSet()
        fetchTopRepos()

        val toUpdate = mongoManager
            .getAllRepos(languages)
            .filter { it.second.githubUpdateDate < LocalDateTime.now().minusDays(1) }
            .groupBy { it.first }
            .mapValues { it.value.map { it.second } }
            .mapValues { it.value.sortedBy { it.githubUpdateDate }.take(500) }

        coroutineScope {
            val updatedMap = updateLeftoverRepos(toUpdate)
            launch {
                mongoManager.updateAll(updatedMap)
            }
            fetchNewRepos()
        }
    }

    private suspend fun fetchTopRepos() {
        logger.info { "Starting to fetch repositories for languages: $languages" }
        coroutineScope {
            languages.forEach { lang ->
                val fetchedRepos = fetchLanguage(lang, null)
                minimumStarsFound[lang] = fetchedRepos.minOfOrNull { it.stargazers } ?: return@forEach
                launch {
                    mongoManager.updateLanguage(lang, fetchedRepos)
                }
            }
        }
    }

    private suspend fun fetchNewRepos() {
        logger.info { "Starting to fetch new repositories with values $minimumStarsFound" }
        coroutineScope {
            minimumStarsFound.forEach { (lang, maximumStars) ->
                val fetchedRepos = fetchLanguage(lang, maximumStars)
                logger.info { "$lang | Fetching finished, total: ${fetchedRepos.size}" }
                launch {
                    mongoManager.updateLanguage(lang, fetchedRepos)
                }
            }
        }
    }

    private suspend fun fetchLanguage(language: Language, maximumStars: Int?): Set<Repository> {
        val fetcher = Fetcher()
        var cursor = Optional.absent<String>()

        return buildSet {
            do {
                val apiResponse: ApiResponse
                try {
                    apiResponse = fetcher.fetchMostStarredRepos(language, cursor, maximumStars)
                } catch (e: Exception) {
                    if ("403" in e.message.orEmpty()) {
                        logger.error { " 403: Rate limit exceeded" }
                        break
                    }
                    logger.error { " $language | $e " }
                    break
                }

                if (apiResponse.repos.any { it.stargazers < MINIMUM_STARS }) {
                    logger.warn { "Found repo with < $MINIMUM_STARS stars" }
                    break
                }

                addAll(apiResponse.repos)
                cursor = Optional.present(apiResponse.endCursor)
            } while (apiResponse.hasNextPage)
        }
    }

    private suspend fun updateLeftoverRepos(toUpdate: Map<Language, List<Repository>>): Map<Language, Set<Repository>> {
        logger.info { "Starting to update repositories for languages: $languages with ${toUpdate.flatMap { it.value }.size} repos" }
        return buildMap {
            toUpdate.forEach { (lang, repos) ->
                put(lang, updateLeftoverByLanguage(repos))
            }
        }
            .entries
            .associate { it.key to it.value }
    }

    private suspend fun updateLeftoverByLanguage(repos: List<Repository>): Set<Repository> {
        val fetcher = Fetcher()
        return buildSet {
            repos.chunked(50).forEach {
                try {
                    val apiResponse = fetcher.fetchReposToUpdate(it)
                    addAll(apiResponse.repos)
                } catch (e: Exception) {
                    logger.error { "Update error: $e " }
                }
            }
        }
    }
}
