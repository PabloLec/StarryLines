package models

import java.time.LocalDate

data class TopRepository(
    val name: String,
    val description: String,
    val createdAt: LocalDate,
    val stargazers: Int,
    val url: String,
    val loc: Int?,
    val milliStarsPerLine: Int?,
    var score: Int?
) {
    var languagePercent: Int = 0

    companion object {
        fun fromRepository(repo: Repository): TopRepository =
            TopRepository(
                repo.name,
                repo.description,
                repo.createdAt.toLocalDate(),
                repo.stargazers,
                repo.url,
                repo.loc,
                repo.milliStarsPerLine,
                null
            ).also { it.languagePercent = repo.languagePercent }
    }
}
