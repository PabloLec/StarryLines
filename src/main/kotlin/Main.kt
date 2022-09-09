import api.GraphQLClient
import dev.pablolec.starrylines.RepoFilesQuery

suspend fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString()}")

    val client = GraphQLClient.getClient()

    val response = client.query(RepoFilesQuery("pablolec", "recoverpy")).execute()
    print(response.data)
}