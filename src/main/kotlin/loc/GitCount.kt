package loc

import com.github.syari.kgit.KGit
import models.Repository
import models.SupportedLanguage
import mu.KotlinLogging
import java.io.File

class GitCount(val language: String, val repo: Repository) {
    private val logger = KotlinLogging.logger {}
    private var lineCount = 0
    private val directory = File("/tmp/loc/${repo.name}")

    fun run(): Int {
        logger.info { "Start GitCount: ${repo.name} last update ${repo.locUpdateDate}" }
        try {
            clone()
            count()
        } catch (e: Throwable) {
            logger.error { "Error cloning ${repo.name} ${e.message}" }
            throw e
        } finally {
            directory.deleteRecursively()
        }
        return lineCount
    }

    private fun clone() {
        if (!isFreeSpaceEnough()) throw Exception("Not enough free space")
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
        val extensions = SupportedLanguage.valueOf(language.uppercase()).extensions()
        val parser = SupportedLanguage.valueOf(language.uppercase()).commentParser()
        directory.walk().forEach { file ->
            if (extensions.none { extension -> file.name.lowercase().endsWith(extension) }) {
                return@forEach
            }
            if (!file.isFile) return@forEach

            lineCount += try {
                parser(file.bufferedReader())
            } catch (e: VirtualMachineError) {
                logger.error { "Error counting ${repo.url} file ${file.name} ${e.message}" }
                file.readText().length
            }
        }
        lineCount /= 80
        logger.info { "Count succeeded for ${repo.name} | Total LoC: $lineCount" }
    }

    private fun isFreeSpaceEnough(): Boolean {
        val freeSpace = File("/").freeSpace * 0.9
        val requiredSpace = repo.diskUsage * 1024
        logger.debug { "Free space: $freeSpace | Required space: $requiredSpace" }
        return freeSpace > requiredSpace
    }
}
