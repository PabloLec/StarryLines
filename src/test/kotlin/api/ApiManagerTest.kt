package api

import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.testing.enqueueTestResponse
import db.MongoClient
import db.MongoManager
import dev.pablolec.starrylines.GetTopReposQuery
import dev.pablolec.starrylines.UpdateReposQuery
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mocks.apolloClientMocked
import mocks.repoToInsertBeforeUpdate
import mocks.testDataFewStars
import mocks.testDataTopReposQuery
import mocks.testDataUpdateReposQuery
import models.Language
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class, ApolloExperimental::class)
internal class ApiManagerTest {
    private val mongoManager = MongoManager()
    private val updateQuery = UpdateReposQuery(listOf("testId"))

    init {
        mockkObject(GraphQLClient)
        every { GraphQLClient.getClient() } returns apolloClientMocked
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun testSimpleRun() = runTest {
        apolloClientMocked.enqueueTestResponse(getTopQuery("kotlin"), testDataTopReposQuery)

        ApiManager(mongoManager, setOf(Language.KOTLIN)).run()
        val allRepos = mongoManager.getAllRepos(setOf(Language.KOTLIN)).map { it.second.name }

        assertContains(allRepos, "Repo1")
    }

    @Test
    fun testRunWithUpdate() = runTest {
        apolloClientMocked.enqueueTestResponse(getTopQuery("kotlin"), testDataTopReposQuery)
        apolloClientMocked.enqueueTestResponse(updateQuery, testDataUpdateReposQuery)
        MongoClient.insertOne(repoToInsertBeforeUpdate, "kotlin")

        ApiManager(mongoManager, setOf(Language.KOTLIN)).run()
        val allRepos = mongoManager.getAllRepos(setOf(Language.KOTLIN)).map { it.second }
        val updatedRepo = allRepos.find { it.name == "repo_to_update" }!!

        assertEquals(20000, updatedRepo.stargazers)
        assert(updatedRepo.githubUpdateDate != repoToInsertBeforeUpdate.githubUpdateDate)
    }

    @Test
    fun testRunWithFewStars() = runTest {
        apolloClientMocked.enqueueTestResponse(getTopQuery("java"), testDataFewStars)

        ApiManager(mongoManager, setOf(Language.JAVA)).run()

        val allRepos = mongoManager.getAllRepos(setOf(Language.JAVA)).map { it.second.name }

        assert(allRepos.isEmpty())
    }

    private fun getTopQuery(language: String) =
        GetTopReposQuery("sort:stars stars:>1 language:$language", Optional.absent())

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() = runTest {
            MongoClient.deleteCollection("kotlin")
        }

        @JvmStatic
        @AfterAll
        fun cleanUp() = runTest {
            MongoClient.deleteCollection("kotlin")
            unmockkAll()
        }
    }
}
