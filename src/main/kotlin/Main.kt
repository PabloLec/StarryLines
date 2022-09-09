import api.Fetcher

suspend fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString()}")

    val testFetcher = Fetcher("javascript")
    testFetcher.fetchMostStarredRepos()
}