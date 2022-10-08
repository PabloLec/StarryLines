package top

import db.MongoClient
import models.Language
import models.TopRepository

class TopFactory {
    fun createTop(language: Language, blacklist: List<String>) =
        MongoClient.getCollection(language.toString())
            .asSequence()
            .filterNot { it.loc == null || it.milliStarsPerLine == null || it.url in blacklist }
            .map { TopRepository.fromRepository(it) }
            .onEach { repo -> repo.score = getRealMilliStars(repo) }
            .sortedByDescending { it.score }
            .take(100)
            .onEachIndexed { index, repo -> repo.rank = index + 1 }
            .toSet()

    private fun getRealMilliStars(repo: TopRepository) =
        repo.milliStarsPerLine
            .div(100.0)
            .times(repo.languagePercent)
            .toInt()
}
