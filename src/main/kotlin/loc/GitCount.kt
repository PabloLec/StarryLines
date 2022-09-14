package loc

import com.github.syari.kgit.KGit
import models.Repository
import models.SupportedLanguage
import mu.KotlinLogging
import org.eclipse.jgit.api.errors.JGitInternalException
import java.io.File

class GitCount(val language: String, val repo: Repository) {
    private val logger = KotlinLogging.logger {}
    private var lineCount = 0
    private val directory = File("/tmp/loc/${repo.name}")

    fun run(): Int? {
        logger.info { "Start GitCount: ${repo.name} last update ${repo.locUpdateDate}" }
        try {
            clone()
            count()
        } catch (e: JGitInternalException) {
            logger.error { "Error cloning ${repo.name} ${e.message}" }
            return null
        } catch (e: VirtualMachineError) {
            logger.error { "Error counting ${repo.name} ${e.message}" }
            return null
        } finally {
            directory.deleteRecursively()
        }
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
        val extensions = SupportedLanguage.valueOf(language.uppercase()).extensions()
        val regex = SupportedLanguage.valueOf(language.uppercase()).commentRegex()
        directory.walk().forEach { file ->
            if (extensions.none { extension -> file.name.endsWith(extension) }) {
                return@forEach
            }
            if (!file.isFile) return@forEach

            val content = file.bufferedReader()
                .readLines()
                .joinToString()

            try {
                content.replace(regex, "")
                    .lines()
                    .map(String::trim)
                    .filterNot(String::isEmpty)
                    .forEach { lineCount += it.count() }
            } catch (e: VirtualMachineError) {
                logger.error { "Error while counting lines of ${repo.url} file ${file.name}" }
                lineCount += content.length
            }
        }
        lineCount /= 80
        logger.info { "Count succeeded for ${repo.name} | Total LoC: $lineCount" }
    }
}
