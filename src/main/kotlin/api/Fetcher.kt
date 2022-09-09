package api

import com.apollographql.apollo3.api.Optional
import dev.pablolec.starrylines.MostStarredReposQuery
import models.Repository
import javax.swing.text.html.Option


class Fetcher(val language: String) {
    private val client = GraphQLClient.getClient()
    private val query = "sort:stars stars:>1 language:${language.trim().lowercase()}"
    private val fetchedRepos = mutableListOf<Repository>()

    suspend fun fetchMostStarredRepos(cursorString: String? = null): MostStarredReposQuery.Search? {
        val cursor = if (cursorString == null) Optional.absent() else Optional.present(cursorString)
        val response =  client.query(MostStarredReposQuery(query, cursor)).execute()
        print(response)
        for (edge in response.data?.search?.edges!!) {
            fetchedRepos.add(Repository.fromEdge(edge!!))
        }
        print("=====================================")
        print(fetchedRepos)
        return response.data?.search
    }


}