package loc

import db.MongoClient
import db.MongoManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import models.Language
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
        val locManager = LocManager(mongoManager, setOf(Language.C, Language.PYTHON))
        MongoClient.insertOne(locCRepo, "c")
        MongoClient.insertOne(locPythonRepo, "python")

        locManager.run()
        var allRepos = mongoManager.getAllRepos(setOf(Language.C, Language.PYTHON)).map { it.second }
        while (allRepos.any { it.loc == null }) {
            // Hacky way to ensure MongoDB has time to update the documents
            allRepos = mongoManager.getAllRepos(setOf(Language.C, Language.PYTHON)).map { it.second }
        }
        val pythonRepo = allRepos.find { it.name == "pythonRepo" }!!
        val cRepo = allRepos.find { it.name == "CRepo" }!!

        assertEquals(pythonRepo.loc, 256)
        assertEquals(cRepo.loc, 28341)
    }

    @Test
    fun testFail() = runTest {
        // Test that the LocManager doesn't crash when it can't find a repo
        val locManager = LocManager(mongoManager, setOf(Language.PYTHON))
        MongoClient.insertOne(pythonRepo, "python")

        locManager.run()
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
            MongoClient.deleteCollection("python")
            MongoClient.deleteCollection("c")
            MongoClient.deleteCollection("blacklist")
            File("/tmp/loc/").deleteRecursively()
        }
    }
}
