package top

import db.MongoClient
import models.Language
import models.TopRepository

class TopFactory {
    fun createTop(language: Language) =
        MongoClient.getCollection(language.toString())
            .asSequence()
            .map { TopRepository.fromRepository(it) }
            .filterNot { it.loc == null || it.milliStarsPerLine == null }
            .filter { isEligible(it) }
            .onEach { repo -> repo.score = getRealMilliStars(repo) }
            .sortedByDescending { it.score }
            .take(100)
            .toSet()

    private fun getRealMilliStars(repo: TopRepository) =
        (repo.milliStarsPerLine?.div(100.0))?.times(repo.languagePercent)
            ?.toInt()

    private fun isEligible(repo: TopRepository) = listOf(
        repo.loc!! > 10,
        "awesome" !in repo.url,
        "curated" !in repo.description.lowercase(),
        "obsolete" !in repo.description.lowercase(),
        "cheatsheet" !in repo.description.lowercase()
    )
        .all { it }
}
