package top

import api.Fetcher
import db.MongoManager
import models.Language
import models.TopRepository
import mu.KotlinLogging
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class TopManager(private val mongoManager: MongoManager, val languages: Set<Language>) {
    private val logger = KotlinLogging.logger {}
    suspend fun run() {
        saveTops(setUpdateDates(getTops()))
    }

    private fun getTops(): Set<Pair<Language, Set<TopRepository>>> {
        val topFactory = TopFactory()
        val blacklist = mongoManager.getBlacklist()
        return buildSet {
            languages.forEach {
                add(Pair(it, topFactory.createTop(it, blacklist)))
            }
        }
    }

    private suspend fun saveTops(tops: Set<Pair<Language, Set<TopRepository>>>) {
        tops.forEach {
            mongoManager.updateTop(it.first.toString().plus("_top"), it.second.toList())
        }
    }

    private suspend fun setUpdateDates(tops: Set<Pair<Language, Set<TopRepository>>>): Set<Pair<Language, Set<TopRepository>>> {
        val fetcher = Fetcher()

        tops.forEach { top ->
            fetcher.fetchUpdateDates(top.second).forEach { date ->
                top.second.find { it.ghid == date.first }!!.updatedAt = dateToHumanReadable(date.second)
            }
            top.second.forEach {
                if (it.updatedAt == null) {
                    it.updatedAt = "Unknown"
                    mongoManager.addToBlacklist(it, "EXCEPTION: No update date")
                }
            }
        }

        return tops
    }

    private fun dateToHumanReadable(date: String): String? {
        if (date == "null") {
            logger.warn { "Date is null" }
            return null
        }
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val then = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)

        return when (val diff = ChronoUnit.DAYS.between(then, now)) {
            in 0..1 -> "Today"
            in 1..2 -> "Yesterday"
            in 2..7 -> "$diff days ago"
            in 7..14 -> "Last week"
            in 14..30 -> "${diff.floorDiv(7)} weeks ago"
            in 30..60 -> "Last month"
            in 60..365 -> "${diff.floorDiv(30)} months ago"
            in 365..730 -> "Last year"
            else -> "${diff.floorDiv(365)} years ago"
        }
    }
}
