package db

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import test.mocks.*
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
        MongoClient.insertOne(javaRepo, "java_test")
        javaRepo.locUpdateDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        javaRepo.loc = 1000
        javaRepo.milliStarsPerLine = 10000
        mongoManager.updateLoc(javaRepo, "java_test")
        assertContains(MongoClient.getCollection("java_test"), javaRepo)
    }

    @Test
    fun testUpdateAll() = runTest {
        mongoManager.addToBlacklist(blacklistRepo, null)
        mongoManager.updateAll(fetchResult)
        assertContains(MongoClient.getCollection("java_test"), javaRepo)
        assertContains(MongoClient.getCollection("python_test"), pythonRepo)
        assert(!MongoClient.getCollection("python_test").contains(blacklistRepo))
    }

    @Test
    fun testUpdateTop() = runTest {
        mongoManager.updateTop("top_test", listOf(topRepo1, topRepo2))
        assertContains(MongoClient.getTopCollection("top_test"), topRepo1)
        assertContains(MongoClient.getTopCollection("top_test"), topRepo2)
    }

    @Test
    fun testUpdateCollectionsWithBlacklist() {
        runTest {
            mongoManager.addToBlacklist(blacklistRepo, null)
            MongoClient.insertOne(blacklistRepo, "java_test")
            assertContains(MongoClient.getCollection("java_test").map { it.name }, blacklistRepo.name)
            mongoManager.updateCollectionsWithBlacklist()
            assert(!MongoClient.getCollection("java_test").map { it.name }.contains(blacklistRepo.name))
        }
    }

    @Test
    fun testGetAllRepos() = runTest {
        mongoManager.updateAll(fetchResult)
        val allRepos = mongoManager.getAllRepos(setOf("java_test", "python_test"))
        assertContains(allRepos, Pair("java_test", javaRepo))
        assertContains(allRepos, Pair("python_test", pythonRepo))
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
            MongoClient.deleteCollection("java_test")
            MongoClient.deleteCollection("python_test")
            MongoClient.deleteCollection("top_test")
        }
    }
}
