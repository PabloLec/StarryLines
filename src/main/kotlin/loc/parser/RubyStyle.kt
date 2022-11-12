package loc.parser

import models.LocParseResult
import java.io.BufferedReader

fun parseRubyStyle(reader: BufferedReader): LocParseResult {
    var line: String?
    var lineCount = 0
    var parsedLength = 0
    var isInComment = false

    while (reader.readLine().also { line = it } != null) {
        line = line!!.trim()
        when {
            line.isNullOrEmpty() -> continue
            line!!.startsWith("=begin") -> isInComment = true
            isInComment && line!!.startsWith("=end") -> isInComment = false
            isInComment -> continue
            else -> {
                val lineLength = removeInlineCommentsShellStyle(line!!).length
                parsedLength += lineLength
                if (lineLength > 0) {
                    lineCount += 1
                }
            }
        }
    }
    return LocParseResult(lineCount, parsedLength)
}
