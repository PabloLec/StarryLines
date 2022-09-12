package db

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import models.Repository
import mu.KotlinLogging

class MongoManager(private val languagesMap: Map<String, Set<Repository>>) {
    private val logger = KotlinLogging.logger {}

    suspend fun updateAll() {
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
        val jobs = mutableListOf<Deferred<Unit>>()
        coroutineScope {
            repos.forEach {
                jobs.add(
                    async {
                        MongoClient.upsertFromGHApi(it, language)
                    }
                )
            }
            jobs.awaitAll()
            logger.info { "Mongo update finished for $language" }
        }
    }
}
