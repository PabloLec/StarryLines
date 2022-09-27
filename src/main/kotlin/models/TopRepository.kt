package models

import java.time.format.DateTimeFormatter

data class TopRepository(
    var rank: Int?,
    val name: String,
    val description: String,
    val createdAt: String,
    val stargazers: Int,
    val url: String,
    val loc: Int,
    val languagePercent: Int,
    val milliStarsPerLine: Int,
    var score: Int?
) {

    companion object {
        private val formatter: DateTimeFormatter? = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        fun fromRepository(repo: Repository): TopRepository =
            TopRepository(
                null,
                repo.name,
                repo.description,
                repo.createdAt.format(formatter),
                repo.stargazers,
                repo.url,
                repo.loc!!,
                repo.languagePercent,
                repo.milliStarsPerLine!!,
                null
            )
    }
}
