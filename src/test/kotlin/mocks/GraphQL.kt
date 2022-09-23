package test.mocks

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.testing.QueueTestNetworkTransport
import dev.pablolec.starrylines.GetTopReposQuery
import dev.pablolec.starrylines.UpdateReposQuery
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@OptIn(ApolloExperimental::class)
val apolloClientMocked = ApolloClient.Builder()
    .networkTransport(QueueTestNetworkTransport())
    .build()

val testPageInfo = GetTopReposQuery.PageInfo(
    hasNextPage = false,
    startCursor = null,
    endCursor = "null"
)

val testEdgeNewRepo = GetTopReposQuery.Edge(
    node = GetTopReposQuery.Node(
        __typename = "Repository",
        onRepository = GetTopReposQuery.OnRepository(
            id = "MDEwOlJlcG9zaXRvcnkxMjM0NTY3ODk=",
            name = "Repo1",
            description = "Description1",
            createdAt = "2021-01-01T00:00:00Z",
            stargazers = GetTopReposQuery.Stargazers(
                totalCount = 10000
            ),
            url = "",
            defaultBranchRef = GetTopReposQuery.DefaultBranchRef(
                name = "main"
            ),
            languages = GetTopReposQuery.Languages(
                totalSize = 1,
                edges = listOf(
                    GetTopReposQuery.Edge1(
                        size = 1,
                        node = GetTopReposQuery.Node1(
                            name = "Kotlin"
                        )
                    )
                )
            ),
            diskUsage = 1
        )
    )
)

val testNodeRepoToUpdate = listOf(
    UpdateReposQuery.Node(
        __typename = "Repository",
        onRepository = UpdateReposQuery.OnRepository(
            id = "MDEwOlJlcG9zaXRvcnkxMjM0NTY3ODk=",
            name = "repo_to_update",
            description = "Description1",
            createdAt = "2021-01-01T00:00:00Z",
            stargazers = UpdateReposQuery.Stargazers(
                totalCount = 20000
            ),
            url = "repo_to_update",
            defaultBranchRef = UpdateReposQuery.DefaultBranchRef(
                name = "main"
            ),
            languages = UpdateReposQuery.Languages(
                totalSize = 1,
                edges = listOf(
                    UpdateReposQuery.Edge(
                        size = 1,
                        node = UpdateReposQuery.Node1(
                            name = "Kotlin"
                        )
                    )
                )
            ),
            diskUsage = 1
        )
    )
)

val repoToInsertBeforeUpdate = models.Repository(
    "testId",
    "repo_to_update",
    "test",
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    10000,
    "repo_to_update",
    "main",
    100,
    1,
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusDays(2),
    null,
    null,
    null
)

val testSearchTopRepos = GetTopReposQuery.Search(testPageInfo, 10, listOf(testEdgeNewRepo))

val testDataTopReposQuery = GetTopReposQuery.Data(testSearchTopRepos, null)

val testDataUpdateReposQuery = UpdateReposQuery.Data(testNodeRepoToUpdate)
