package loc

import db.MongoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.GitCountResult
import models.Language
import models.Repository
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocManager(private val mongoManager: MongoManager, val languages: Set<Language>) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun run() {
        clearLocDir()
        val dispatcher = Dispatchers.IO.limitedParallelism(10)
        runBlocking {
            getReposToProcess().forEach { repo ->
                launch(dispatcher) {
                    updateLocCount(repo.second, repo.first)
                }
            }
        }
    }

    private suspend fun updateLocCount(repo: Repository, language: Language) {
        val count: GitCountResult
        try {
            count = GitCount(language, repo).run()
        } catch (e: Exception) {
            if (setOf("Symbolic link loop", "Repository not found").any { e.message?.contains(it) == true }) {
                mongoManager.addToBlacklist(repo, "EXCEPTION: ${e.message}")
            }
            return
        }
        repo.loc = count.lineCount
        repo.parsedLength = count.parsedLength
        repo.locUpdateDate = LocalDateTime.now(ZoneOffset.UTC)
        mongoManager.updateLoc(repo, language)
    }

    private fun getReposToProcess() = mongoManager.getAllRepos(languages).filter { it.second.loc == null }
        .plus(mongoManager.getAllRepos(languages).sortedBy { it.second.locUpdateDate }
            .filterNot {
                it.second.locUpdateDate != null && it.second.locUpdateDate!!.isAfter(
                    LocalDateTime.now().minusHours(8)
                )
            })

    private fun clearLocDir() = File("${System.getProperty("java.io.tmpdir")}/loc/").deleteRecursively()
}
