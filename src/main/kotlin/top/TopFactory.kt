package top

import models.Repository
import models.TopRepository

class TopFactory {
    fun createTop(repos: List<Repository>, blacklist: List<String>) =
        repos.asSequence()
            .filterNot { it.loc == null || it.milliStarsPerLine == null || it.url in blacklist }
            .map { TopRepository.fromRepository(it) }
            .onEach { repo -> repo.score = getScore(repo) }
            .sortedByDescending { it.score }
            .take(100)
            .onEachIndexed { index, repo -> repo.rank = index + 1 }
            .toSet()

    private fun getScore(repo: TopRepository) =
        repo.milliStarsPerLine
            .div(100.0)
            .times(repo.languagePercent)
            .toInt()
}
