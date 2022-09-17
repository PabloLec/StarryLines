package loc

import db.MongoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.Repository
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocManager(private val mongoManager: MongoManager, val languages: Set<String>) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun run() {
        val dispatcher = Dispatchers.IO.limitedParallelism(10)
        runBlocking {
            mongoManager.getAllRepos(languages).forEach { repo ->
                launch(dispatcher) {
                    updateLocCount(repo.second, repo.first)
                }
            }
        }
    }

    private suspend fun updateLocCount(repo: Repository, language: String) {
        val count = GitCount(language, repo).run() ?: return mongoManager.addToBlacklist(repo)
        repo.loc = count
        repo.locUpdateDate = LocalDateTime.now(ZoneOffset.UTC)
        mongoManager.updateLoc(repo, language)
    }
}
