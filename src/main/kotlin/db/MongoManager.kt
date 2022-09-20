package db

import kotlinx.coroutines.*
import models.Repository
import models.TopRepository
import mu.KotlinLogging

class MongoManager {
    private val logger = KotlinLogging.logger {}

    suspend fun addToBlacklist(repo: Repository, reason: String?) = coroutineScope {
        MongoClient.insertOneToBlacklist(repo.url, reason)
    }

    fun getBlacklist(): List<String> = MongoClient.getBlacklistCollection()

    suspend fun updateLoc(repo: Repository, language: String) =
        coroutineScope { MongoClient.updateFromLoc(repo, language) }

    suspend fun updateAll(languagesMap: Map<String, Set<Repository>>) {
        logger.info { "Starting updateAll" }
        val jobs = mutableListOf<Deferred<Unit>>()
        coroutineScope {
            languagesMap.forEach { (language, repositories) ->
                jobs.add(
                    async {
                        updateLanguage(language, repositories)
                    }
                )
            }
            jobs.awaitAll()
        }
    }

    private suspend fun updateLanguage(language: String, repos: Set<Repository>) {
        val blackList = getBlacklist()
        val jobs = mutableListOf<Deferred<Unit>>()
        coroutineScope {
            repos.forEach { repo ->
                jobs.add(
                    async {
                        if (blackList.any { it == repo.url }) {
                            logger.info { "Skipping ${repo.url} because it's in the blacklist" }
                            return@async
                        }
                        MongoClient.upsertFromGHApi(repo, language)
                    }
                )
            }
            jobs.awaitAll()
            logger.info { "Mongo update finished for $language" }
        }
    }

    suspend fun updateTop(collectionName: String, repos: List<TopRepository>) {
        MongoClient.deleteCollection(collectionName)
        MongoClient.insertMany(repos, collectionName)
    }

    suspend fun updateCollectionsWithBlacklist() {
        val blacklist = getBlacklist()
        val collections = MongoClient.getAllCollections()
        val jobs = mutableListOf<Deferred<Unit>>()
        runBlocking {
            collections
                .filterNot { "blacklist" in it || "_top" in it }
                .forEach { collection ->
                    jobs.add(
                        async {
                            MongoClient.deleteManyByUrl(blacklist, collection)
                        }
                    )
                }
            jobs.awaitAll()
            logger.info { "Blacklist update finished" }
        }
    }

    fun getAllRepos(languages: Set<String>) = languages.map { Pair(it, MongoClient.getCollection(it)) }
        .flatMap { it.second.map { repo -> Pair(it.first, repo) } }
        .sortedBy { it.second.locUpdateDate }
}
