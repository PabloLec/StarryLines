package loc

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
        val gitCount = GitCount("c", locCRepo)
        assert(gitCount.run() == 28341)
    }

    @Test
    fun testRunOnPython() {
        val gitCount = GitCount("python", locPythonRepo)
        assert(gitCount.run() == 161)
    }

    @Test
    fun testRepoTooBig() {
        assertFails { GitCount("python", locPythonRepo.copy().also { it.diskUsage = Integer.MAX_VALUE }).run() }
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            File("/tmp/loc/").deleteRecursively()
        }

        @JvmStatic
        @AfterAll
        fun cleanUp() {
            File("/tmp/loc/").deleteRecursively()
        }
    }
}
