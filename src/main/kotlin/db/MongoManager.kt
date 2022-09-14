package db

import kotlinx.coroutines.*
import models.Repository
import mu.KotlinLogging

class MongoManager {
    private val logger = KotlinLogging.logger {}

    suspend fun addToBlacklist(repo: Repository) = coroutineScope {
        MongoClient.insertOne(repo, "blacklist")
    }

    private fun getBlacklist(): List<Repository> = MongoClient.getCollection("blacklist")

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
                        if (blackList.any { it.url == repo.url }) {
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

    suspend fun updateCollectionsWithBlacklist() {
        val blacklist = getBlacklist()
        val collections = MongoClient.getAllCollections()
        val jobs = mutableListOf<Deferred<Unit>>()
        runBlocking {
            collections.filterNot { it == "blacklist" }.forEach { collection ->
                jobs.add(
                    async {
                        MongoClient.deleteMany(blacklist, collection)
                    }
                )
            }
            jobs.awaitAll()
            logger.info { "Blacklist update finished" }
        }
    }
}
