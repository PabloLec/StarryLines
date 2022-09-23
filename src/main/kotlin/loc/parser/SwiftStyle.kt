package loc.parser

import java.io.BufferedReader

fun parseSwiftStyle(reader: BufferedReader): Int {
    var line: String?
    var parsedLength = 0

    var nestedCommentsLevel = 0
    while (reader.readLine().also { line = it } != null) {
        line = line!!.trim()

        when {
            line.isNullOrEmpty() -> continue
            line!!.startsWith("/*") && line!!.endsWith("*/") -> continue
            line!!.startsWith("/*") -> nestedCommentsLevel++
            nestedCommentsLevel > 0 && line!!.contains("*/") -> nestedCommentsLevel--
            nestedCommentsLevel > 0 -> continue
            line!!.startsWith("//") -> continue
            else -> parsedLength += removeInlineCommentsCStyle(line!!).length
        }
    }
    return parsedLength
}