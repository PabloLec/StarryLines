package loc

import models.Repository
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

internal class GitCountTest {

    @Test
    fun testRunOnC() {
        val testRepo = Repository(
            "repo_test_c", "repo_test_c", "",
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 0,
            "https://github.com/PabloLec/sl_test_c", "v2", 0, 0,
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), null, 0, 0
        )
        val gitCount = GitCount("c", testRepo)
        assert(gitCount.run() == 28341)
    }

    @Test
    fun testRunOnPython() {
        val testRepo = Repository(
            "repo_test_python", "repo_test_python", "",
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 0,
            "https://github.com/PabloLec/sl_test_python", "master", 0, 0,
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), null, 0, 0
        )
        val gitCount = GitCount("python", testRepo)
        assert(gitCount.run() == 45)
    }
}
