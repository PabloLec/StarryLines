package db

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import test.mocks.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
internal class MongoManagerTest {
    @InjectMockKs
    lateinit var mongoManager: MongoManager

    @Test
    fun testGetBlackList() =
        assertEquals(mongoManager.getBlacklist().map { it }, listOf("test1", "test2"))

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
        javaRepo.mStarsPerLine = 10000
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

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            MockKAnnotations.init(this)
        }

        @JvmStatic
        @AfterAll
        fun cleanUp(): Unit = runTest {
            MongoClient.deleteMany(listOf(blacklistRepo), "blacklist")
            MongoClient.deleteCollection("java_test")
            MongoClient.deleteCollection("python_test")
            MongoClient.deleteCollection("top_test")
        }
    }
}
