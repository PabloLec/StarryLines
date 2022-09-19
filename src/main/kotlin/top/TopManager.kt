package top

import db.MongoManager
import models.TopRepository

class TopManager(private val mongoManager: MongoManager, val languages: Set<String>) {
    suspend fun run() = saveTops(getTops())

    private fun getTops(): Set<Pair<String, Set<TopRepository>>> {
        val topFactory = TopFactory()
        return buildSet {
            languages.forEach {
                add(Pair(it, topFactory.createTop(it)))
            }
        }
    }

    private suspend fun saveTops(tops: Set<Pair<String, Set<TopRepository>>>) {
        tops.forEach {
            mongoManager.updateTop(it.first.plus("_top"), it.second.toList())
        }
    }
}
