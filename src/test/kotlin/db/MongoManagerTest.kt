package db

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import models.Language
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import test.mocks.blacklistRepo
import test.mocks.blacklistRepoPrevious1
import test.mocks.blacklistRepoPrevious2
import test.mocks.fetchResult
import test.mocks.javaRepo
import test.mocks.pythonRepo
import test.mocks.topRepo1
import test.mocks.topRepo2
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class MongoManagerTest {
    private var mongoManager = MongoManager()

    @Test
    fun testGetBlackList() =
        assertEquals(
            mongoManager.getBlacklist().map { it },
            listOf(blacklistRepoPrevious1.url, blacklistRepoPrevious2.url)
        )

    @Test
    fun testAddToBlacklist() = runTest {
        mongoManager.addToBlacklist(blacklistRepo, null)
        assertContains(mongoManager.getBlacklist().map { it }, blacklistRepo.name)
    }

    @Test
    fun testUpdateLoc() = runTest {
        MongoClient.insertOne(javaRepo, "java")
        javaRepo.locUpdateDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        javaRepo.loc = 1000
        javaRepo.milliStarsPerLine = 10000
        mongoManager.updateLoc(javaRepo, Language.JAVA)
        assertContains(MongoClient.getCollection("java"), javaRepo)
    }

    @Test
    fun testUpdateAll() = runTest {
        mongoManager.addToBlacklist(blacklistRepo, null)
        mongoManager.updateAll(fetchResult)
        assertContains(MongoClient.getCollection("java"), javaRepo)
        assertContains(MongoClient.getCollection("python"), pythonRepo)
        assert(!MongoClient.getCollection("python").contains(blacklistRepo))
    }

    @Test
    fun testUpdateTop() = runTest {
        mongoManager.updateTop("top", listOf(topRepo1, topRepo2))
        assertContains(MongoClient.getTopCollection("top"), topRepo1)
        assertContains(MongoClient.getTopCollection("top"), topRepo2)
    }

    @Test
    fun testUpdateCollectionsWithBlacklist() {
        runTest {
            mongoManager.addToBlacklist(blacklistRepo, null)
            MongoClient.insertOne(blacklistRepo, "java")
            assertContains(MongoClient.getCollection("java").map { it.name }, blacklistRepo.name)
            mongoManager.updateCollectionsWithBlacklist()
            assert(!MongoClient.getCollection("java").map { it.name }.contains(blacklistRepo.name))
        }
    }

    @Test
    fun testGetAllRepos() = runTest {
        mongoManager.updateAll(fetchResult)
        val allRepos = mongoManager.getAllRepos(setOf(Language.JAVA, Language.PYTHON))
        assertContains(allRepos, Pair(Language.JAVA, javaRepo))
        assertContains(allRepos, Pair(Language.PYTHON, pythonRepo))
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() = runTest {
            MongoClient.deleteCollection("blacklist")
            MongoClient.insertOneToBlacklist(blacklistRepoPrevious1.url, "test")
            MongoClient.insertOneToBlacklist(blacklistRepoPrevious2.url, "test")
        }

        @JvmStatic
        @AfterAll
        fun cleanUp() = runTest {
            MongoClient.deleteCollection("blacklist")
            MongoClient.deleteCollection("java")
            MongoClient.deleteCollection("python")
            MongoClient.deleteCollection("top")
        }
    }
}
