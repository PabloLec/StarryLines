package top

import db.MongoClient
import db.MongoManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import models.Language
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import test.mocks.blacklistRepo
import test.mocks.javaRepo
import test.mocks.pythonRepo

@OptIn(ExperimentalCoroutinesApi::class)
internal class TopManagerTest {
    private val mongoManager = MongoManager()

    @Test
    fun testRun() = runTest {
        MongoClient.insertOne(javaRepo.copy().also { it.loc = 1500; it.milliStarsPerLine = 8500 }, "kotlin")
        MongoClient.insertOne(pythonRepo.copy().also { it.loc = 3500; it.milliStarsPerLine = 15800 }, "kotlin")
        MongoClient.insertOne(blacklistRepo.copy().also { it.loc = 0; it.milliStarsPerLine = 0 }, "kotlin")
        TopManager(mongoManager, setOf(Language.KOTLIN)).run()

        val top = MongoClient.getTopCollection("kotlin_top")
        assert(listOf("pythonRepo", "javaRepo").all { it in top.map { it.name } })
        assert("blacklistRepo" !in top.map { it.name })
        assert(top.find { it.name == "pythonRepo" }?.score!! > top.find { it.name == "javaRepo" }?.score!!)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() = runTest {
            MongoClient.deleteCollection("kotlin")
            MongoClient.deleteCollection("kotlin_top")
        }

        @JvmStatic
        @AfterAll
        fun cleanUp() = runTest {
            MongoClient.deleteCollection("kotlin")
            MongoClient.deleteCollection("kotlin_top")
        }
    }
}
