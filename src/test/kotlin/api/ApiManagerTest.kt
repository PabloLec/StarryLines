package api

import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.testing.enqueueTestResponse
import db.MongoClient
import db.MongoManager
import dev.pablolec.starrylines.GetTopReposQuery
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import test.mocks.apolloClientMocked
import test.mocks.testData
import kotlin.test.assertContains

@OptIn(ExperimentalCoroutinesApi::class, ApolloExperimental::class)
internal class ApiManagerTest {
    private var apiManager: ApiManager
    private val mongoManager = MongoManager()

    init {
        apiManager = ApiManager(mongoManager, setOf("kotlin_test"))
        mockkObject(GraphQLClient)
        every { GraphQLClient.getClient() } returns apolloClientMocked
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun run() = runTest {
        val testQuery = GetTopReposQuery("sort:stars stars:>1 language:kotlin_test", Optional.absent())
        apolloClientMocked.enqueueTestResponse(testQuery, testData)

        apiManager.run()
        val allRepos = mongoManager.getAllRepos(setOf("kotlin_test")).map { it.second.name }
        assertContains(allRepos, "Repo1")
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() = runTest {
            MongoClient.deleteCollection("kotlin_test")
        }

        @JvmStatic
        @AfterAll
        fun cleanUp() = runTest {
            MongoClient.deleteCollection("kotlin_test")
            unmockkAll()
        }
    }
}
