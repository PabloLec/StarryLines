package cli

import models.Language

enum class Action {
    FETCH, // Fetch GH API
    GETLOC, // Get LoC for stored repos
    TOP; // Create Top 100

    val args: MutableSet<Language> = mutableSetOf()
}

fun parseArgs(args: Array<String>): Action {
    if (args.isEmpty()) throw IllegalArgumentException("No action specified")

    return when (args[0].uppercase().trim()) {
        Action.FETCH.name -> {
            parseAction(Action.FETCH, args)
        }

        Action.GETLOC.name -> {
            parseAction(Action.GETLOC, args)
        }

        Action.TOP.name -> {
            parseAction(Action.TOP, args)
        }

        else -> throw IllegalArgumentException("Unknown action: ${args[0]}")
    }
}

fun parseAction(action: Action, args: Array<String>): Action {
    if (args.size < 2) throw IllegalArgumentException("No language specified")
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
