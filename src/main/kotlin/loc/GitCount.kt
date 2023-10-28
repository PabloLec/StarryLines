package loc

import com.github.syari.kgit.KGit
import models.GitCountResult
import models.Language
import models.Repository
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.util.*

class GitCount(val language: Language, val repo: Repository) {
    private val logger = KotlinLogging.logger {}
    private var lineCount = 0
    private var parsedLength = 0
    private val directory = getTmpDirectory()

    fun run(): GitCountResult {
        logger.info { "Start GitCount: ${repo.name} | last GH update: ${repo.updatedAt} | Last LoC update: ${repo.locUpdateDate}" }
        try {
            clone()
            count()
        } catch (e: Throwable) {
            logger.error { "Error cloning ${repo.name} ${e.message}" }
            throw e
        } finally {
            directory.parentFile.deleteRecursively()
            logger.info { "Directory deleted: $directory" }
        }
        return GitCountResult(lineCount, parsedLength)
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
        val languageString = language.toString().uppercase()
        val extensions = Language.valueOf(languageString).extensions()
        val parser = Language.valueOf(languageString).commentParser()
        directory.walk().forEach { file ->
            if (!isFileParsable(file, extensions)) return@forEach
            try {
                val result = parser(file.bufferedReader())
                lineCount += result.lineCount
                parsedLength += result.parsedLength
            } catch (e: VirtualMachineError) {
                logger.error(e) { "Error counting ${repo.url} file ${file.name}" }
                lineCount += file.readText().lines().size
                parsedLength += file.readText().length
            } finally {
                file.delete()
            }
        }
        parsedLength /= 80
        logger.info { "Count succeeded for ${repo.name} | Total LoC: $lineCount | Parsed LoC: $parsedLength" }
    }

    private fun isFileParsable(file: File, extensions: Set<String>): Boolean =
        when {
            file.name.lowercase().endsWith(".lock") -> false
            Files.isSymbolicLink(file.toPath()) || (file.isFile && extensions.none { extension ->
                file.name.lowercase().endsWith(extension)
            }) -> {
                file.delete()
                false
            }

            !file.isFile -> false
            else -> true
        }

    private fun isFreeSpaceEnough(): Boolean {
        val freeSpace = File("/").freeSpace * 0.9
        val requiredSpace = repo.diskUsage.toDouble() * 1024
        return freeSpace > requiredSpace
    }

    private fun getTmpDirectory(): File =
        File("${System.getProperty("java.io.tmpdir")}/loc/${UUID.randomUUID()}/${repo.name}")
}
