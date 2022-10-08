package models

import dev.pablolec.starrylines.GetTopReposQuery
import dev.pablolec.starrylines.UpdateReposQuery
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.ceil

data class Repository(
    val ghid: String,
    val name: String,
    var description: String,
    val createdAt: LocalDateTime,
    val stargazers: Int,
    val url: String,
    var defaultBranch: String,
    var languagePercent: Int,
    var diskUsage: Int,
    val githubUpdateDate: LocalDateTime,
    var locUpdateDate: LocalDateTime?,
    var loc: Int?,
    var milliStarsPerLine: Int?,
    var descriptionLanguage: String?,
    var translatedDescription: String?
) {
    fun computeMilliStarsPerLine(): Int? = loc?.let { if (loc == 0) 0 else (stargazers * 1000) / it }

    companion object {
        private val dateTimeRegex = Regex("""(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})""")

        fun fromNode(node: UpdateReposQuery.Node): Repository {
            fun Any.toDate(): LocalDateTime {
                val dateValues = dateTimeRegex.find(this as String)!!.groupValues
                    .drop(1).dropLast(1).map { it.toInt() }.toTypedArray()
                return LocalDateTime.of(dateValues[0], dateValues[1], dateValues[2], dateValues[3], dateValues[4])
            }

            fun getLanguagePercent(languages: UpdateReposQuery.Languages?): Int {
                languages!!.edges!!.maxByOrNull { it!!.size }!!.let {
                    return ceil(((it.size.toDouble() / languages.totalSize) * 100)).toInt()
                }
            }

            return Repository(
                node.onRepository!!.id,
                node.onRepository.name.trim(),
                node.onRepository.description?.trim() ?: "",
                node.onRepository.createdAt.toDate(),
                node.onRepository.stargazers.totalCount,
                node.onRepository.url as String,
                node.onRepository.defaultBranchRef!!.name,
                getLanguagePercent(node.onRepository.languages),
                node.onRepository.diskUsage!!,
                LocalDateTime.now(ZoneOffset.UTC),
                null,
                null,
                null,
                null,
                null
            )
        }

        fun fromEdge(edge: GetTopReposQuery.Edge): Repository {
            fun Any.toDate(): LocalDateTime {
                val dateValues = dateTimeRegex.find(this as String)!!.groupValues
                    .drop(1).dropLast(1).map { it.toInt() }.toTypedArray()
                return LocalDateTime.of(dateValues[0], dateValues[1], dateValues[2], dateValues[3], dateValues[4])
            }

            fun getLanguagePercent(languages: GetTopReposQuery.Languages?): Int {
                languages!!.edges!!.maxByOrNull { it!!.size }!!.let {
                    return ceil(((it.size.toDouble() / languages.totalSize) * 100)).toInt()
                }
            }

            return Repository(
                edge.node!!.onRepository!!.id,
                edge.node.onRepository!!.name.trim(),
                edge.node.onRepository.description?.trim() ?: "",
                edge.node.onRepository.createdAt.toDate(),
                edge.node.onRepository.stargazers.totalCount,
                edge.node.onRepository.url as String,
                edge.node.onRepository.defaultBranchRef!!.name,
                getLanguagePercent(edge.node.onRepository.languages),
                edge.node.onRepository.diskUsage!!,
                LocalDateTime.now(ZoneOffset.UTC),
                null,
                null,
                null,
                null,
                null
            )
        }
    }
}
