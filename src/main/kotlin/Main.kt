import api.ApiManager
import db.MongoClient
import db.MongoManager
import loc.LocManager
import kotlin.system.exitProcess

enum class Action {
    FETCH, // Fetch GH API
    GETLOC; // Get LoC for stored repos

    val args: MutableSet<String> = mutableSetOf()
}

enum class SupportedLanguages {
    JAVASCRIPT {
        override fun extensions() = setOf(".js")
        override fun commentRegex() = Regex("""/\*(.|[\r\n])*?\*/|[^:]//.*""", RegexOption.MULTILINE)
    },
    KOTLIN {
        override fun extensions() = setOf(".kt")
        override fun commentRegex() = Regex("""/\*(.|[\r\n])*?\*/|[^:]//.*""", RegexOption.MULTILINE)
    },
    JAVA {
        override fun extensions() = setOf(".java")
        override fun commentRegex() = Regex("""/\*(.|[\r\n])*?\*/|[^:]//.*""", RegexOption.MULTILINE)
    },
    PYTHON {
        override fun extensions() = setOf(".py")
        override fun commentRegex() = Regex("""[^:]#.*|([^(.]\"\"\"[^(]*)\"\"\"""", RegexOption.MULTILINE)
    };

    abstract fun extensions(): Set<String>
    abstract fun commentRegex(): Regex
}

suspend fun main(args: Array<String>) {
    when (val action = parseArgs(args)) {
        Action.FETCH -> {
            val languagesMap = ApiManager(action.args).run()
            MongoManager(languagesMap).updateAll()
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
        action.args.addAll(SupportedLanguages.values().map { it.name.lowercase() })
        return action
    }
    args.drop(1).forEach {
        val language = it.lowercase().trim()
        if (language !in SupportedLanguages.values().map { it.name.lowercase() }) {
            throw IllegalArgumentException("Unsupported language: $language")
        }
        action.args.add(language)
    }
    return action
}
