package loc

import db.MongoClient
import db.MongoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.Repository
import java.time.LocalDateTime

class LocManager(private val mongoManager: MongoManager, val languages: Set<String>) {
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
        val count = GitCount(language, repo).run() ?: return mongoManager.addToBlacklist(repo)
        repo.loc = count
        repo.locUpdateDate = LocalDateTime.now()
        mongoManager.updateLoc(repo, language)
    }

    private fun getRepos() = languages.map { Pair(it, MongoClient.getCollection(it)) }
        .flatMap { it.second.map { repo -> Pair(it.first, repo) } }
        .sortedBy { it.second.locUpdateDate }
}
