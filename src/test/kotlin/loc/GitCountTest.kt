package loc

import db.MongoClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mocks.locCRepo
import mocks.locPythonRepo
import models.GitCountResult
import models.Language
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertFails

internal class GitCountTest {

    @Test
    fun testRunOnC() {
        val gitCount = GitCount(Language.C, locCRepo)
        assert(gitCount.run() == GitCountResult(49835, 28340))
    }

    @Test
    fun testRunOnPython() {
        val gitCount = GitCount(Language.PYTHON, locPythonRepo)
        assert(gitCount.run() == GitCountResult(409, 256))
    }

    @Test
    fun testRepoTooBig() {
        assertFails {
            GitCount(
                Language.PYTHON,
                locPythonRepo.copy().also { it.diskUsage = Integer.MAX_VALUE }
            ).run()
        }
    }

    companion object {
        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        @BeforeAll
        fun setUp() = runTest {
            File("/tmp/loc/").deleteRecursively()
            MongoClient.deleteCollection("c")
            MongoClient.deleteCollection("python")
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        @AfterAll
        fun cleanUp() = runTest {
            File("/tmp/loc/").deleteRecursively()
            MongoClient.deleteCollection("blacklist")
            MongoClient.deleteCollection("c")
            MongoClient.deleteCollection("python")
        }
    }
}
