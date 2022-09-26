package loc

import db.MongoClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import models.Language
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import test.mocks.locCRepo
import test.mocks.locPythonRepo
import java.io.File
import kotlin.test.assertFails

internal class GitCountTest {

    @Test
    fun testRunOnC() {
        val gitCount = GitCount(Language.C, locCRepo)
        assert(gitCount.run() == 28341)
    }

    @Test
    fun testRunOnPython() {
        val gitCount = GitCount(Language.PYTHON, locPythonRepo)
        assert(gitCount.run() == 256)
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
        @JvmStatic
        @BeforeAll
        fun setUp() {
            File("/tmp/loc/").deleteRecursively()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        @AfterAll
        fun cleanUp() = runTest {
            File("/tmp/loc/").deleteRecursively()
            MongoClient.deleteCollection("blacklist")
        }
    }
}
