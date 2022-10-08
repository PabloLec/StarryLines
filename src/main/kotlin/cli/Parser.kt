package cli

import models.Language

enum class Action {
    FETCH, // Fetch GH API
    GETLOC, // Get LoC for stored repos
    TOP, // Create Top 100
    TRANSLATE; // Translate repos description
    val args: MutableSet<Language> = mutableSetOf()
}

fun parseArgs(args: Array<String>): Action {
    require(args.isNotEmpty()) { "No action specified" }
    require(args[0].uppercase().trim() in Action.values().map { it.name }) { "Invalid action specified" }
    return when (Action.valueOf(args[0].uppercase().trim())) {
        Action.FETCH -> {
            parseAction(Action.FETCH, args)
        }

        Action.GETLOC -> {
            parseAction(Action.GETLOC, args)
        }

        Action.TOP -> {
            parseAction(Action.TOP, args)
        }

        Action.TRANSLATE -> {
            parseAction(Action.TRANSLATE, args)
        }
    }
}

fun parseAction(action: Action, args: Array<String>): Action {
    require(args.size > 1) { "No language specified" }
    if (args[1] == "all") {
        action.args.addAll(Language.values())
        return action
    }
    args.drop(1).forEach {
        Language.find(it.trim().lowercase())?.let { lang ->
            action.args.add(lang)
        } ?: throw IllegalArgumentException("Unsupported language: $it")
    }
    return action
}
