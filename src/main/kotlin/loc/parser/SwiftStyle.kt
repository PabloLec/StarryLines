package loc.parser

import models.LocParseResult
import java.io.BufferedReader

fun parseSwiftStyle(reader: BufferedReader): LocParseResult {
    var line: String?
    var lineCount = 0
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
            else -> {
                val lineLength = removeInlineCommentsCStyle(line!!).length
                parsedLength += lineLength
                if (lineLength > 0) {
                    lineCount += 1
                }
            }
        }
    }
    return LocParseResult(lineCount, parsedLength)
}
