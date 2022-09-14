import api.ApiManager
import db.MongoClient
import db.MongoManager
import loc.LocManager
import models.SupportedLanguage
import kotlin.system.exitProcess

enum class Action {
    FETCH, // Fetch GH API
    GETLOC; // Get LoC for stored repos

    val args: MutableSet<String> = mutableSetOf()
}

suspend fun main(args: Array<String>) {
    val mongoManager = MongoManager()
    mongoManager.updateCollectionsWithBlacklist()
    when (val action = parseArgs(args)) {
        Action.FETCH -> {
            val languagesMap = ApiManager(action.args).run()
            mongoManager.updateAll(languagesMap)
            MongoClient.close()
            exitProcess(0)
        }

        Action.GETLOC -> {
            LocManager(action.args).run()
        }
    }
}

private fun parseArgs(args: Array<String>): Action {
    if (args.isEmpty()) throw IllegalArgumentException("No action specified")

    return when (args[0].uppercase().trim()) {
        Action.FETCH.name -> {
            parseAction(Action.FETCH, args)
        }

        Action.GETLOC.name -> {
            parseAction(Action.GETLOC, args)
        }

        else -> throw IllegalArgumentException("Unknown action: ${args[0]}")
    }
}

private fun parseAction(action: Action, args: Array<String>): Action {
    if (args.size < 2) throw IllegalArgumentException("No language specified")
    if (args[1] == "all") {
        action.args.addAll(SupportedLanguage.values().map { it.name.lowercase() })
        return action
    }
    args.drop(1).forEach {
        val language = it.lowercase().trim()
        if (language !in SupportedLanguage.values().map { it.name.lowercase() }) {
            throw IllegalArgumentException("Unsupported language: $language")
        }
        action.args.add(language)
    }
    return action
}
