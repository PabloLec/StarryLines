package loc

import SupportedLanguages
import com.github.syari.kgit.KGit
import models.Repository
import mu.KotlinLogging
import java.io.File

class GitCount(val language: String, val repo: Repository) {
    private val logger = KotlinLogging.logger {}
    private var lineCount = 0
    private val directory = File("/tmp/loc/${repo.name}")

    fun run(): Int {
        logger.info { "Start GitCount: ${repo.name} last update ${repo.locUpdateDate}" }
        clone()
        try {
            count()
        } catch (e: Exception) {
            logger.error(e) { "Error while counting lines of code of ${repo.url}" }
        }

        directory.deleteRecursively()
        return lineCount
    }

    private fun clone() {
        logger.info { "Cloning ${repo.url} branch ${repo.defaultBranch}" }
        KGit.cloneRepository {
            setURI(repo.url)
            setDirectory(directory)
            setBranchesToClone(listOf("refs/heads/${repo.defaultBranch}"))
            setBranch("refs/heads/${repo.defaultBranch}")
            setTimeout(600)
        }
        logger.info { "Clone succeeded for ${repo.url} branch ${repo.defaultBranch}" }
    }

    private fun count() {
        logger.info { "Counting lines of code for ${repo.name}" }
        val extensions = SupportedLanguages.valueOf(language.uppercase()).extensions()
        val regex = SupportedLanguages.valueOf(language.uppercase()).commentRegex()
        directory.walk().forEach { file ->
            if (extensions.none { extension -> file.name.endsWith(extension) }) {
                return@forEach
            }
            if (!file.isFile) return@forEach

            file.bufferedReader()
                .readLines()
                .joinToString()
                .replace(regex, "")
                .lines()
                .map(String::trim)
                .filterNot(String::isEmpty)
                .forEach { lineCount += it.count() }
        }
        lineCount /= 80
        logger.info { "Count succeeded for ${repo.name} | Total LoC: $lineCount" }
    }
}
