package models

import dev.pablolec.starrylines.MostStarredReposQuery
import java.time.LocalDateTime

data class Repository(
    val name: String,
    var description: String,
    val createdAt: LocalDateTime,
    var stargazers: Int,
    val url: String,
    var defaultBranch: String,
    val githubUpdateDate: LocalDateTime,
    val locUpdateDate: LocalDateTime,
    val loc: Int
) {
    companion object {
        private val dateTimeRegex = Regex("""(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})""")

        fun fromEdge(edge: MostStarredReposQuery.Edge): Repository {
            fun Any.toDate(): LocalDateTime {
                val dateValues = dateTimeRegex.find(this as String)!!.groupValues
                    .drop(1).dropLast(1).map { it.toInt() }.toTypedArray()
                return LocalDateTime.of(dateValues[0], dateValues[1], dateValues[2], dateValues[3], dateValues[4])
            }

            return Repository(
                edge.node!!.onRepository!!.name.trim(),
                edge.node.onRepository?.description?.trim() ?: "",
                edge.node.onRepository!!.createdAt.toDate(),
                edge.node.onRepository.stargazers.totalCount,
                edge.node.onRepository.url as String,
                edge.node.onRepository.defaultBranchRef!!.name,
                LocalDateTime.now(),
                LocalDateTime.MIN,
                0
            )
        }
    }
}
