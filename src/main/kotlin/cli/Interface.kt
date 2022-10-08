package cli

import api.ApiManager
import db.MongoClient
import db.MongoManager
import loc.LocManager
import top.TopManager
import translation.TranslationManager
import kotlin.system.exitProcess

suspend fun main(args: Array<String>) {
    val mongoManager = MongoManager()
    mongoManager.updateCollectionsWithBlacklist()
    when (val action = parseArgs(args)) {
        Action.FETCH -> {
            ApiManager(mongoManager, action.args).run()
        }

        Action.GETLOC -> {
            LocManager(mongoManager, action.args).run()
        }

        Action.TOP -> {
            TopManager(mongoManager, action.args).run()
        }

        Action.TRANSLATE -> {
            TranslationManager(mongoManager, action.args).run()
        }
    }
    MongoClient.close()
    exitProcess(0)
}
