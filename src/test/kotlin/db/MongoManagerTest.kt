package db

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import models.Repository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import kotlin.test.assertContains
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class MongoManagerTest {
    @InjectMockKs
    lateinit var mongoManager: MongoManager

    @Test
    fun testGetBlackList() =
        assertEquals(mongoManager.getBlacklist().map { it }, listOf("test1", "test2"))

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAddToBlacklist() = runTest {
        mongoManager.addToBlacklist(blackListRepo, null)
        assertContains(mongoManager.getBlacklist().map { it }, blackListRepo.name)
    }

    @Test
    fun testUpdateLoc() {
    }

    @Test
    fun testUpdateAll() {
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUpdateCollectionsWithBlacklist() {
        runTest {
            mongoManager.addToBlacklist(blackListRepo, null)
            MongoClient.insertOne(blackListRepo, "java_test")
            assertContains(MongoClient.getCollection("java_test").map { it.name }, blackListRepo.name)
            mongoManager.updateCollectionsWithBlacklist()
            assert(!MongoClient.getCollection("java_test").map { it.name }.contains(blackListRepo.name))
        }
    }

    companion object {
        val blackListRepo = Repository(
            "", "test3", "", LocalDateTime.now(), 0, "test3", "", 0,
            0,
            LocalDateTime.now(), LocalDateTime.now(), 0, null
        )

        @JvmStatic
        @BeforeAll
        fun setUp() {
            MockKAnnotations.init(this)
        }

        @JvmStatic
        @OptIn(ExperimentalCoroutinesApi::class)
        @AfterAll
        fun cleanUp(): Unit = runTest {
            MongoClient.deleteMany(listOf(blackListRepo), "blacklist")
            MongoClient.deleteMany(listOf(blackListRepo), "java_test")
        }
    }
}
