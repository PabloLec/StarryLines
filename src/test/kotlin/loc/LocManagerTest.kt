package loc

import db.MongoClient
import db.MongoManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import mocks.locCRepo
import mocks.locPythonRepo
import mocks.pythonRepo
import models.Language
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class LocManagerTest {
    private val mongoManager = MongoManager()

    @Test
    fun testRun() = runBlocking {
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

        assertEquals(pythonRepo.loc, 409)
        assertEquals(pythonRepo.parsedLength, 256)
        assertEquals(cRepo.loc, 49835)
        assertEquals(cRepo.parsedLength, 28340)
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
