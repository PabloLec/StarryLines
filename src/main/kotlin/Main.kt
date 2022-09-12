import api.ApiManager
import db.MongoClient
import db.MongoManager
import kotlin.system.exitProcess

enum class Actions {
    FETCH; // Fetch GH API

    val args: MutableSet<String> = mutableSetOf()
}

enum class SupportedLanguages {
    JAVASCRIPT,
    KOTLIN,
    JAVA
}

suspend fun main(args: Array<String>) {
    when (val action = parseArgs(args)) {
        Actions.FETCH -> {
            val apiManager = ApiManager(action.args)
            val languagesMap = apiManager.run()
            MongoManager.updateAll(languagesMap)
            MongoClient.close()
            exitProcess(0)
        }
    }
}

private fun parseArgs(args: Array<String>): Actions {
    if (args.isEmpty()) throw IllegalArgumentException("No action specified")

    val action: Actions
    when (args[0].uppercase().trim()) {
        Actions.FETCH.name -> {
            if (args.size < 2) throw IllegalArgumentException("No language specified")
            action = Actions.FETCH
            args.drop(1).forEach {
                val language = it.lowercase().trim()
                if (language !in SupportedLanguages.values().map { it.name.lowercase() }) {
                    throw IllegalArgumentException("Unsupported language: $language")
                }
                action.args.add(language)
            }
            return action
        }

        else -> throw IllegalArgumentException("Unknown action: ${args[0]}")
    }
}
