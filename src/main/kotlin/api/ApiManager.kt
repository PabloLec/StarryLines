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

const val LIMIT_PER_LANGUAGE = 2000
const val MINIMUM_STARS = 350

class ApiManager(private val mongoManager: MongoManager, val languages: Set<Language>) {
    private val logger = KotlinLogging.logger {}
    private val minimumStarsFound = mutableMapOf<Language, Int>()

    suspend fun run() {
        fetchTopRepos()

        val toUpdate = mongoManager
            .getAllRepos(languages)
            .filter { it.second.githubUpdateDate < LocalDateTime.now().minusDays(1) }
            .groupBy { it.first }
            .mapValues { it.value.map { it.second } }

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
                launch {
                    mongoManager.updateLanguage(lang, fetchedRepos)
                }
            }
        }
    }

    private suspend fun fetchLanguage(language: Language, maximumStars: Int?): MutableSet<Repository> {
        val fetcher = Fetcher()
        val repos = mutableSetOf<Repository>()
        var cursor = Optional.absent<String>()

        while (repos.size < LIMIT_PER_LANGUAGE) {
            val apiResponse: ApiResponse
            try {
                apiResponse = fetcher.fetchMostStarredRepos(language, cursor, maximumStars)
            } catch (e: Exception) {
                if ("403" in e.message.orEmpty()) {
                    logger.error { " 403: Rate limit exceeded" }
                    break
                }
                logger.error { " $language | $e " }
                return mutableSetOf()
            }

            if (apiResponse.repos.any { it.stargazers < MINIMUM_STARS }) {
                logger.warn { "Found repo with < $MINIMUM_STARS stars" }
                return repos
            }

            repos.addAll(apiResponse.repos)
            if (!apiResponse.hasNextPage) break
            cursor = Optional.present(apiResponse.endCursor)
        }

        logger.info { "$language | Fetching finished, total: ${repos.size}" }
        return repos
    }

    private suspend fun updateLeftoverRepos(toUpdate: Map<Language, List<Repository>>): Map<Language, Set<Repository>> {
        logger.info { "Starting to update repositories for languages: $languages with ${toUpdate.flatMap { it.value }.size} repos" }
        val results = buildMap {
            toUpdate.forEach { (lang, repos) ->
                put(lang, updateLeftoverByLanguage(repos))
            }
        }

        return results.entries.associate { it.key to it.value }
    }

    private suspend fun updateLeftoverByLanguage(repos: List<Repository>): Set<Repository> {
        val fetcher = Fetcher()
        val updatedRepos = mutableSetOf<Repository>()

        repos.chunked(20).forEach {
            val apiResponse: ApiResponse
            try {
                apiResponse = fetcher.fetchReposToUpdate(it)
                updatedRepos.addAll(apiResponse.repos)
            } catch (e: Exception) {
                logger.error { "Update error: $e " }
            }
        }

        return updatedRepos
    }
}
