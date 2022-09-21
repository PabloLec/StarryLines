package loc

import db.MongoClient
import db.MongoManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import test.mocks.locCRepo
import test.mocks.locPythonRepo
import test.mocks.pythonRepo
import java.io.File
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class LocManagerTest {
    private val mongoManager = MongoManager()

    @Test
    fun testRun() = runTest {
        val locManager = LocManager(mongoManager, setOf("c_test", "python_test"))
        MongoClient.insertOne(locCRepo, "c_test")
        MongoClient.insertOne(locPythonRepo, "python_test")

        locManager.run()
        var allRepos = mongoManager.getAllRepos(setOf("c_test", "python_test")).map { it.second }
        if (allRepos.any { it.loc == null }) {
            // Hacky way to ensure MongoDB has time to update the documents
            allRepos = mongoManager.getAllRepos(setOf("c_test", "python_test")).map { it.second }
        }
        val pythonRepo = allRepos.find { it.name == "pythonRepo" }!!
        val cRepo = allRepos.find { it.name == "CRepo" }!!

        assertEquals(pythonRepo.loc, 161)
        assertEquals(cRepo.loc, 28341)
    }

    @Test
    fun testFail() = runTest {
        val locManager = LocManager(mongoManager, setOf("python_test"))
        MongoClient.insertOne(pythonRepo, "python_test")

        locManager.run()

        assert(mongoManager.getBlacklist().contains("pythonRepo"))
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            File("/tmp/loc/").deleteRecursively()
        }

        @JvmStatic
        @AfterAll
        fun cleanUp() = runTest {
            MongoClient.deleteCollection("python_test")
            MongoClient.deleteCollection("c_test")
            MongoClient.deleteCollection("blacklist")
            File("/tmp/loc/").deleteRecursively()
        }
    }
}
