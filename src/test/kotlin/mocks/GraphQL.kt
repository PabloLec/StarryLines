package test.mocks

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.testing.QueueTestNetworkTransport
import dev.pablolec.starrylines.GetTopReposQuery

@OptIn(ApolloExperimental::class)
val apolloClientMocked = ApolloClient.Builder()
    .networkTransport(QueueTestNetworkTransport())
    .build()

val testPageInfo = GetTopReposQuery.PageInfo(
    hasNextPage = false,
    startCursor = null,
    endCursor = "null"
)

val testEdge1 = GetTopReposQuery.Edge(
    node = GetTopReposQuery.Node(
        __typename = "Repository",
        onRepository = GetTopReposQuery.OnRepository(
            id = "MDEwOlJlcG9zaXRvcnkxMjM0NTY3ODk=",
            name = "Repo1",
            description = "Description1",
            createdAt = "2021-01-01T00:00:00Z",
            stargazers = GetTopReposQuery.Stargazers(
                totalCount = 100
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

val testSearch = GetTopReposQuery.Search(testPageInfo, 10, listOf(testEdge1))

val testData = GetTopReposQuery.Data(testSearch, null)
