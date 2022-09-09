import api.Fetcher

enum class SupportedLanguages {
    JAVASCRIPT,
    KOTLIN,
    JAVA
}

suspend fun main(args: Array<String>) {
    when {
        args.isEmpty() -> throw IllegalArgumentException("No language specified")
        args.size > 1 -> throw IllegalArgumentException("Too many arguments")
        args[0] !in SupportedLanguages.values()
            .map { it.name.lowercase() } -> throw IllegalArgumentException("Unsupported language: ${args[0]}")
    }

    val language = args[0].uppercase()

    val testFetcher = Fetcher(language)
    testFetcher.fetchMostStarredRepos()
}
