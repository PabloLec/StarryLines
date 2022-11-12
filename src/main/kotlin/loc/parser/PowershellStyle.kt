package loc.parser

import models.LocParseResult
import java.io.BufferedReader

fun parsePowershellStyle(reader: BufferedReader): LocParseResult {
    var line: String?
    var lineCount = 0
    var parsedLength = 0
    var isInComment = false

    while (reader.readLine().also { line = it } != null) {
        line = line!!.trim()
        when {
            line!!.isEmpty() -> continue
            line!!.startsWith("<#") && line!!.endsWith("#>") -> continue
            isInComment && line!!.contains("#>") -> isInComment = false
            line!!.startsWith("<#") -> isInComment = true
            isInComment -> continue
            line!!.startsWith("#") -> continue
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
