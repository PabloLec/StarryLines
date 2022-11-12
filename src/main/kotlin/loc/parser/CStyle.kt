package loc.parser

import models.LocParseResult
import java.io.BufferedReader

fun parseCStyle(reader: BufferedReader): LocParseResult {
    var line: String?
    var lineCount = 0
    var parsedLength = 0
    var isInComment = false

    while (reader.readLine().also { line = it } != null) {
        line = line!!.trim()
        when {
            line.isNullOrEmpty() -> continue
            line!!.startsWith("/*") && line!!.endsWith("*/") -> continue
            line!!.startsWith("/*") -> isInComment = true
            isInComment && line!!.contains("*/") -> isInComment = false
            isInComment -> continue
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

fun removeInlineCommentsCStyle(line: String): String {
    val start = line.indexOf("/*")
    val end = line.lastIndexOf("*/")
    if (start != -1 && end != -1) {
        return (line.substring(0, start) + line.substring(end + 2)).trim()
    }
    val delimiter = line.lastIndexOf("//")
    if (delimiter != -1 && (line.substring(0, delimiter).count { it == '"' } % 2) == 0) {
        return line.substring(0, delimiter).trim()
    }
    return line.trim()
}
