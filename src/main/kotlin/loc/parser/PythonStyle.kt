package loc.parser

import models.LocParseResult
import java.io.BufferedReader

fun parsePythonStyle(reader: BufferedReader): LocParseResult {
    var line: String?
    var lineCount = 0
    var parsedLength = 0
    var openedParenthesis = false
    var isInComment = false

    while (reader.readLine().also { line = it } != null) {
        line = line!!.trim()
        if (line!!.contains("\"\"\"") || line!!.contains("'''")) openedParenthesis = !openedParenthesis
        when {
            line!!.isEmpty() -> continue
            isInComment && (line!!.contains("\"\"\"") || line!!.contains("'''")) -> isInComment = false
            line!!.startsWith("\"\"\"") && line!!.endsWith("\"\"\"") -> continue
            line!!.startsWith("'''") && line!!.endsWith("'''") -> continue
            (line!!.startsWith("\"\"\"") || line!!.startsWith("'''")) && openedParenthesis -> isInComment = true
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
