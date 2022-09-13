package loc

import db.MongoClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.Repository
import mu.KotlinLogging
import java.time.LocalDateTime

class LocManager(val languages: Set<String>) {
    private val logger = KotlinLogging.logger {}

    @OptIn(ExperimentalCoroutinesApi::class)
    fun run() {
        val dispatcher = Dispatchers.IO.limitedParallelism(10)
        runBlocking {
            getRepos().forEach { repo ->
                launch(dispatcher) {
                    updateLocCount(repo.second, repo.first)
                }
            }
        }
    }

    private suspend fun updateLocCount(repo: Repository, language: String) {
        val count = GitCount(language, repo).run()
        repo.loc = count
        repo.locUpdateDate = LocalDateTime.now()
        MongoClient.updateFromLoc(repo, language)
    }

    private fun getRepos() = languages.map { Pair(it, MongoClient.getAllRepositoriesByLanguage(it)) }
        .flatMap { it.second.map { repo -> Pair(it.first, repo) } }
        .sortedBy { it.second.locUpdateDate }
}
