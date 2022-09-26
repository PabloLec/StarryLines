package top

import db.MongoManager
import models.Language
import models.TopRepository

class TopManager(private val mongoManager: MongoManager, val languages: Set<Language>) {
    suspend fun run() = saveTops(getTops())

    private fun getTops(): Set<Pair<Language, Set<TopRepository>>> {
        val topFactory = TopFactory()
        return buildSet {
            languages.forEach {
                add(Pair(it, topFactory.createTop(it)))
            }
        }
    }

    private suspend fun saveTops(tops: Set<Pair<Language, Set<TopRepository>>>) {
        tops.forEach {
            mongoManager.updateTop(it.first.toString().plus("_top"), it.second.toList())
        }
    }
}
